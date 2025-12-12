/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2018 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.jdt.internal.core.search.indexing;

import static org.eclipse.jdt.internal.core.JavaModelManager.trace;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.internal.compiler.env.AutomaticModuleNaming;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.index.IndexLocation;
import org.eclipse.jdt.internal.core.search.JavaSearchDocument;
import org.eclipse.jdt.internal.core.search.processing.JobManager;

public class AddJarFileToIndex extends BinaryContainer {

    static final public char JAR_SEPARATOR = IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR.charAt(0);

    public IFile resource;

    public IndexLocation indexFileURL;

    final public boolean forceIndexUpdate;

    public AddJarFileToIndex(IFile resource, IndexLocation indexFile, IndexManager manager) {
        this(resource, indexFile, manager, false);
    }

    public AddJarFileToIndex(IFile resource, IndexLocation indexFile, IndexManager manager, final boolean updateIndex) {
        super(resource.getFullPath(), manager);
        this.resource = resource;
        this.indexFileURL = indexFile;
        this.forceIndexUpdate = updateIndex;
    }

    public AddJarFileToIndex(IPath jarPath, IndexLocation indexFile, IndexManager manager) {
        this(jarPath, indexFile, manager, false);
    }

    public AddJarFileToIndex(IPath jarPath, IndexLocation indexFile, IndexManager manager, final boolean updateIndex) {
        // external JAR scenario - no resource
        super(jarPath, manager);
        this.indexFileURL = indexFile;
        this.forceIndexUpdate = updateIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AddJarFileToIndex) {
            if (this.resource != null)
                return this.resource.equals(((AddJarFileToIndex) o).resource);
            if (this.containerPath != null)
                return this.containerPath.equals(((AddJarFileToIndex) o).containerPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.resource != null)
            return this.resource.hashCode();
        if (this.containerPath != null)
            return this.containerPath.hashCode();
        return -1;
    }

    @Override
    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
            return true;
        if (hasPreBuiltIndex()) {
            boolean added = this.manager.addIndex(this.containerPath, this.indexFileURL);
            if (added)
                return true;
            this.indexFileURL = null;
        }
        try {
            // if index is already cached, then do not perform any check
            // MUST reset the IndexManager if a jar file is changed
            Index index = this.manager.getIndexForUpdate(this.containerPath, false, /*do not reuse index file*/
            false);
            if (index != null) {
                if (JobManager.VERBOSE)
                    //$NON-NLS-1$
                    trace("-> no indexing required (index already exists) for " + this.containerPath);
                return true;
            }
            index = this.manager.getIndexForUpdate(this.containerPath, true, /*reuse index file*/
            true);
            if (index == null) {
                if (JobManager.VERBOSE)
                    //$NON-NLS-1$
                    trace("-> index could not be created for " + this.containerPath);
                return true;
            }
            ReadWriteMonitor monitor = index.monitor;
            if (monitor == null) {
                if (JobManager.VERBOSE)
                    //$NON-NLS-1$//$NON-NLS-2$
                    trace("-> index for " + this.containerPath + " just got deleted");
                // index got deleted since acquired
                return true;
            }
            index.separator = JAR_SEPARATOR;
            @SuppressWarnings("resource")
            ZipFile zip = null;
            try {
                // this path will be a relative path to the workspace in case the zipfile in the workspace otherwise it will be a path in the
                // local file system
                Path zipFilePath = null;
                // ask permission to write
                monitor.enterWrite();
                if (this.resource != null) {
                    URI location = this.resource.getLocationURI();
                    if (location == null)
                        return false;
                    if (JavaModelManager.ZIP_ACCESS_VERBOSE)
                        //$NON-NLS-1$	//$NON-NLS-2$
                        trace("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + location.getPath());
                    File file = null;
                    try {
                        file = org.eclipse.jdt.internal.core.util.Util.toLocalFile(location, progressMonitor);
                    } catch (CoreException e) {
                        if (JobManager.VERBOSE) {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            trace("-> failed to index " + location.getPath() + " because of the following exception:", e);
                        }
                    }
                    if (file == null) {
                        if (JobManager.VERBOSE)
                            //$NON-NLS-1$ //$NON-NLS-2$
                            trace("-> failed to index " + location.getPath() + " because the file could not be fetched");
                        return false;
                    }
                    if (JavaModelManager.ZIP_ACCESS_VERBOSE)
                        //$NON-NLS-1$	//$NON-NLS-2$
                        trace("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + this.containerPath);
                    zip = new ZipFile(file);
                    zipFilePath = (Path) this.resource.getFullPath().makeRelative();
                    // absolute path relative to the workspace
                } else {
                    if (JavaModelManager.ZIP_ACCESS_VERBOSE)
                        //$NON-NLS-1$	//$NON-NLS-2$
                        trace("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + this.containerPath);
                    // external file -> it is ok to use toFile()
                    zip = new ZipFile(this.containerPath.toFile());
                    zipFilePath = (Path) this.containerPath;
                }
                if (this.isCancelled) {
                    if (JobManager.VERBOSE)
                        //$NON-NLS-1$ //$NON-NLS-2$
                        trace("-> indexing of " + zip.getName() + " has been cancelled");
                    return false;
                }
                if (JobManager.VERBOSE)
                    //$NON-NLS-1$
                    trace("-> indexing " + zip.getName());
                long initialTime = System.currentTimeMillis();
                // all file names //$NON-NLS-1$
                String[] paths = index.queryDocumentNames("");
                if (paths != null) {
                    int max = paths.length;
                    /* check integrity of the existing index file
					 * if the length is equal to 0, we want to index the whole jar again
					 * If not, then we want to check that there is no missing entry, if
					 * one entry is missing then we recreate the index
					 */
                    //$NON-NLS-1$
                    String EXISTS = "OK";
                    //$NON-NLS-1$
                    String DELETED = "DELETED";
                    SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
                    for (int i = 0; i < max; i++) indexedFileNames.put(paths[i], DELETED);
                    for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                        // iterate each entry to index it
                        ZipEntry ze = e.nextElement();
                        String zipEntryName = ze.getName();
                        if (Util.isClassFileName(zipEntryName) && isValidPackageNameForClassOrisModule(zipEntryName))
                            // the class file may not be there if the package name is not valid
                            indexedFileNames.put(zipEntryName, EXISTS);
                    }
                    // a new file was added
                    boolean needToReindex = indexedFileNames.elementSize != max;
                    if (!needToReindex) {
                        Object[] valueTable = indexedFileNames.valueTable;
                        for (Object v : valueTable) {
                            if (v == DELETED) {
                                // a file was deleted so re-index
                                needToReindex = true;
                                break;
                            }
                        }
                        if (!needToReindex) {
                            if (JobManager.VERBOSE)
                                //$NON-NLS-1$
                                trace(//$NON-NLS-1$
                                "-> no indexing required (index is consistent with library) for " + zip.getName() + " (" + //$NON-NLS-1$
                                (System.currentTimeMillis() - initialTime) + "ms)");
                            // to ensure its placed into the saved state
                            this.manager.saveIndex(index);
                            return true;
                        }
                    }
                }
                // Index the jar for the first time or reindex the jar in case the previous index file has been corrupted
                // index already existed: recreate it so that we forget about previous entries
                SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();
                if (!this.manager.resetIndex(this.containerPath)) {
                    // failed to recreate index, see 73330
                    this.manager.removeIndex(this.containerPath);
                    return false;
                }
                index.separator = JAR_SEPARATOR;
                IPath indexPath = null;
                IndexLocation indexLocation;
                if ((indexLocation = index.getIndexLocation()) != null) {
                    indexPath = indexLocation.getIndexPath();
                }
                boolean hasModuleInfoClass = false;
                for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                    if (this.isCancelled) {
                        if (JobManager.VERBOSE)
                            //$NON-NLS-1$ //$NON-NLS-2$
                            trace("-> indexing of " + zip.getName() + " has been cancelled");
                        return false;
                    }
                    // iterate each entry to index it
                    ZipEntry ze = e.nextElement();
                    String zipEntryName = ze.getName();
                    if (Util.isClassFileName(zipEntryName) && isValidPackageNameForClassOrisModule(zipEntryName)) {
                        hasModuleInfoClass |= zipEntryName.contains(TypeConstants.MODULE_INFO_NAME_STRING);
                        // index only classes coming from valid packages - https://bugs.eclipse.org/bugs/show_bug.cgi?id=293861
                        final byte[] classFileBytes = org.eclipse.jdt.internal.compiler.util.Util.getZipEntryByteContent(ze, zip);
                        JavaSearchDocument entryDocument = new JavaSearchDocument(ze, zipFilePath, classFileBytes, participant);
                        this.manager.indexDocument(entryDocument, participant, index, indexPath);
                    }
                }
                if (!hasModuleInfoClass) {
                    String s;
                    try {
                        s = this.resource == null ? this.containerPath.toOSString() : JavaModelManager.getLocalFile(this.resource.getFullPath()).toPath().toAbsolutePath().toString();
                        char[] autoModuleName = AutomaticModuleNaming.determineAutomaticModuleName(s);
                        final char[] contents = CharOperation.append(CharOperation.append(TypeConstants.AUTOMATIC_MODULE_NAME.toCharArray(), ':'), autoModuleName);
                        // adding only the automatic module entry here - can be extended in the future to include other fields.
                        ZipEntry ze = new ZipEntry(TypeConstants.AUTOMATIC_MODULE_NAME);
                        JavaSearchDocument entryDocument = new JavaSearchDocument(ze, zipFilePath, new String(contents).getBytes(Charset.defaultCharset()), participant);
                        this.manager.indexDocument(entryDocument, participant, index, indexPath);
                    } catch (CoreException e) {
                        if (JobManager.VERBOSE) {
                            //$NON-NLS-1$
                            JavaModelManager.trace("", e);
                        }
                    }
                }
                if (this.forceIndexUpdate) {
                    this.manager.savePreBuiltIndex(index);
                } else {
                    this.manager.saveIndex(index);
                }
                if (JobManager.VERBOSE)
                    //$NON-NLS-1$
                    trace(//$NON-NLS-1$
                    "-> done indexing of " + zip.getName() + " (" + //$NON-NLS-1$
                    (System.currentTimeMillis() - initialTime) + "ms)");
            } finally {
                if (zip != null) {
                    if (JavaModelManager.ZIP_ACCESS_VERBOSE) {
                        //$NON-NLS-1$	//$NON-NLS-2$
                        trace("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Closing ZipFile " + this.containerPath);
                    }
                    zip.close();
                }
                // free write lock
                monitor.exitWrite();
            }
        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                //$NON-NLS-1$
                org.eclipse.jdt.internal.core.util.Util.log(Status.info("Can not index not existing zip " + this.containerPath));
            } else if ("zip file is empty".equals(e.getMessage())) {
                //$NON-NLS-1$
                //$NON-NLS-1$
                org.eclipse.jdt.internal.core.util.Util.log(Status.info("Can not index empty zip " + this.containerPath));
            } else {
                //$NON-NLS-1$
                org.eclipse.jdt.internal.core.util.Util.log(e, "Failed to index " + this.containerPath);
            }
            this.manager.removeIndex(this.containerPath);
            return false;
        }
        return true;
    }

    @Override
    public String getJobFamily() {
        if (this.resource != null)
            return super.getJobFamily();
        // external jar
        return this.containerPath.toOSString();
    }

    @Override
    protected Integer updatedIndexState() {
        Integer updateState = null;
        if (hasPreBuiltIndex()) {
            updateState = IndexManager.REUSE_STATE;
        } else {
            updateState = IndexManager.REBUILDING_STATE;
        }
        return updateState;
    }

    @Override
    public String toString() {
        //$NON-NLS-1$
        return "indexing " + this.containerPath.toString();
    }

    protected boolean hasPreBuiltIndex() {
        return !this.forceIndexUpdate && (this.indexFileURL != null && this.indexFileURL.exists());
    }
}

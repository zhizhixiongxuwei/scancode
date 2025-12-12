/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2013 IBM Corporation and others.
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
package org.eclipse.jdt.internal.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;
import org.osgi.service.prefs.BackingStoreException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UserLibraryManager {

    //$NON-NLS-1$
    public final static String CP_USERLIBRARY_PREFERENCES_PREFIX = JavaCore.PLUGIN_ID + ".userLibrary.";

    final public Map<String, UserLibrary> userLibraries = new ConcurrentHashMap<>();

    /*
	 * Gets the library for a given name or <code>null</code> if no such library exists.
	 */
    public synchronized UserLibrary getUserLibrary(String libName) {
        return this.userLibraries.get(libName);
    }

    /*
	 * Returns the names of all defined user libraries. The corresponding classpath container path
	 * is the name appended to the CONTAINER_ID.
	 */
    public synchronized String[] getUserLibraryNames() {
        Set<String> set = this.userLibraries.keySet();
        return set.toArray(String[]::new);
    }

    public UserLibraryManager() {
        IEclipsePreferences instancePreferences = JavaModelManager.getJavaModelManager().getInstancePreferences();
        String[] propertyNames;
        try {
            propertyNames = instancePreferences.keys();
        } catch (BackingStoreException e) {
            //$NON-NLS-1$
            Util.log(e, "Exception while initializing user libraries");
            return;
        }
        boolean preferencesNeedFlush = false;
        for (String propertyName : propertyNames) {
            if (propertyName.startsWith(CP_USERLIBRARY_PREFERENCES_PREFIX)) {
                String propertyValue = instancePreferences.get(propertyName, null);
                if (propertyValue != null) {
                    String libName = propertyName.substring(CP_USERLIBRARY_PREFERENCES_PREFIX.length());
                    StringReader reader = new StringReader(propertyValue);
                    UserLibrary library;
                    try {
                        library = UserLibrary.createFromString(reader);
                    } catch (IOException | ClasspathEntry.AssertionFailedException e) {
                        //$NON-NLS-1$
                        Util.log(e, "Exception while initializing user library " + libName);
                        instancePreferences.remove(propertyName);
                        preferencesNeedFlush = true;
                        continue;
                    }
                    this.userLibraries.put(libName, library);
                }
            }
        }
        if (preferencesNeedFlush) {
            try {
                instancePreferences.flush();
            } catch (BackingStoreException e) {
                //$NON-NLS-1$
                Util.log(e, "Exception while flusing instance preferences");
            }
        }
    }

    public void updateUserLibrary(String libName, String encodedUserLibrary) {
        try {
            // find affected projects
            IPath containerPath = new Path(JavaCore.USER_LIBRARY_CONTAINER_ID).append(libName);
            IJavaProject[] allJavaProjects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
            ArrayList affectedProjects = new ArrayList();
            for (IJavaProject javaProject : allJavaProjects) {
                IClasspathEntry[] entries = javaProject.getRawClasspath();
                for (IClasspathEntry entry : entries) {
                    if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                        if (containerPath.equals(entry.getPath())) {
                            affectedProjects.add(javaProject);
                            break;
                        }
                    }
                }
            }
            // decode user library
            UserLibrary userLibrary = encodedUserLibrary == null ? null : UserLibrary.createFromString(new StringReader(encodedUserLibrary));
            synchronized (this) {
                // update user libraries map
                if (userLibrary != null) {
                    this.userLibraries.put(libName, userLibrary);
                } else {
                    this.userLibraries.remove(libName);
                }
            }
            // update affected projects
            int length = affectedProjects.size();
            if (length == 0)
                return;
            IJavaProject[] projects = new IJavaProject[length];
            affectedProjects.toArray(projects);
            IClasspathContainer[] containers = new IClasspathContainer[length];
            if (userLibrary != null) {
                UserLibraryClasspathContainer container = new UserLibraryClasspathContainer(libName);
                for (int i = 0; i < length; i++) {
                    containers[i] = container;
                }
            }
            JavaCore.setClasspathContainer(containerPath, projects, containers, null);
        } catch (JavaModelException e) {
            //$NON-NLS-1$ //$NON-NLS-2$
            Util.log(e, "Exception while setting user library '" + libName + "'.");
        } catch (IOException | ClasspathEntry.AssertionFailedException ase) {
            //$NON-NLS-1$ //$NON-NLS-2$
            Util.log(ase, "Exception while decoding user library '" + libName + "'.");
        }
    }

    public void removeUserLibrary(String libName) {
        synchronized (this.userLibraries) {
            IEclipsePreferences instancePreferences = JavaModelManager.getJavaModelManager().getInstancePreferences();
            String propertyName = CP_USERLIBRARY_PREFERENCES_PREFIX + libName;
            instancePreferences.remove(propertyName);
            try {
                instancePreferences.flush();
            } catch (BackingStoreException e) {
                //$NON-NLS-1$
                Util.log(e, "Exception while removing user library " + libName);
            }
        }
        // this.userLibraries was updated during the PreferenceChangeEvent (see preferenceChange(...))
    }

    public void setUserLibrary(String libName, IClasspathEntry[] entries, boolean isSystemLibrary) {
        synchronized (this.userLibraries) {
            IEclipsePreferences instancePreferences = JavaModelManager.getJavaModelManager().getInstancePreferences();
            String propertyName = CP_USERLIBRARY_PREFERENCES_PREFIX + libName;
            try {
                String propertyValue = UserLibrary.serialize(entries, isSystemLibrary);
                // sends out a PreferenceChangeEvent (see preferenceChange(...))
                instancePreferences.put(propertyName, propertyValue);
            } catch (IOException e) {
                //$NON-NLS-1$
                Util.log(e, "Exception while serializing user library " + libName);
                return;
            }
            try {
                instancePreferences.flush();
            } catch (BackingStoreException e) {
                //$NON-NLS-1$
                Util.log(e, "Exception while saving user library " + libName);
            }
        }
        // this.userLibraries was updated during the PreferenceChangeEvent (see preferenceChange(...))
    }
}

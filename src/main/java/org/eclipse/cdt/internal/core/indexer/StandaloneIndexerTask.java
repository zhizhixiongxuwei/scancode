/**
 * ****************************************************************************
 *  Copyright (c) 2006, 2020 Wind River Systems, Inc. and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      Markus Schorn - initial API and implementation
 * 	   IBM Corporation
 *      Alexander Fedorov (ArSysOp) - Bug 561992
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.indexer;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.internal.core.index.IWritableIndex;
import org.eclipse.cdt.internal.core.pdom.AbstractIndexerTask;
import org.eclipse.cdt.internal.core.pdom.IndexerProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A task for index updates.
 *
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will work or
 * that it will remain the same. Please do not use this API without consulting
 * with the CDT team.
 * </p>
 *
 * @since 4.0
 */
public abstract class StandaloneIndexerTask extends AbstractIndexerTask {

    public StandaloneIndexer fIndexer;

    public IParserLogService fLogger;

    public static final int[] IDS_FOR_LINKAGES_TO_INDEX = { ILinkage.CPP_LINKAGE_ID, ILinkage.C_LINKAGE_ID, ILinkage.FORTRAN_LINKAGE_ID };

    protected StandaloneIndexerTask(StandaloneIndexer indexer, Collection<String> added, Collection<String> changed, Collection<String> removed, boolean isFast) {
        super(concat(added, changed), removed.toArray(), new StandaloneIndexerInputAdapter(indexer), isFast);
        fIndexer = indexer;
        setShowActivity(fIndexer.getShowActivity());
        setShowProblems(fIndexer.getShowProblems());
        setSkipReferences(fIndexer.getSkipReferences());
        if (getIndexAllFiles()) {
            setIndexFilesWithoutBuildConfiguration(true);
            setIndexHeadersWithoutContext(UnusedHeaderStrategy.useDefaultLanguage);
        } else {
            setIndexFilesWithoutBuildConfiguration(false);
            setIndexHeadersWithoutContext(UnusedHeaderStrategy.skip);
        }
    }

    private static Object[] concat(Collection<?> added, Collection<?> changed) {
        Object[] result = new Object[added.size() + changed.size()];
        int i = 0;
        for (Iterator<?> iterator = added.iterator(); iterator.hasNext(); ) {
            result[i++] = iterator.next();
        }
        for (Iterator<?> iterator = changed.iterator(); iterator.hasNext(); ) {
            result[i++] = iterator.next();
        }
        return result;
    }

    /**
     * Return the indexer.
     */
    final public StandaloneIndexer getIndexer() {
        return fIndexer;
    }

    /**
     * Return indexer's progress information.
     */
    @Override
    final public IndexerProgress getProgressInformation() {
        return super.getProgressInformation();
    }

    /**
     * Figures out whether all files (sources without config, headers not included)
     * should be parsed.
     * @since 4.0
     */
    final protected boolean getIndexAllFiles() {
        return getIndexer().getIndexAllFiles();
    }

    @Override
    protected final IWritableIndex createIndex() {
        return fIndexer.getIndex();
    }

    public final void run(IProgressMonitor monitor) throws InterruptedException {
        long start = System.currentTimeMillis();
        runTask(monitor);
        traceEnd(start);
    }

    protected void traceEnd(long start) {
        if (fIndexer.getTraceStatistics()) {
            IndexerProgress info = getProgressInformation();
            String name = getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1);
            trace(//$NON-NLS-1$
            name + " " + " (" + info.fCompletedSources + //$NON-NLS-1$ //$NON-NLS-2$
            " sources, " + info.fCompletedHeaders + //$NON-NLS-1$
            " headers)");
            boolean allFiles = getIndexAllFiles();
            boolean skipRefs = fIndexer.getSkipReferences() == StandaloneIndexer.SKIP_ALL_REFERENCES;
            boolean skipTypeRefs = skipRefs || fIndexer.getSkipReferences() == StandaloneIndexer.SKIP_TYPE_REFERENCES;
            trace(//$NON-NLS-1$
            name + " Options: " + "parseAllFiles=" + //$NON-NLS-1$
            allFiles + ",skipReferences=" + //$NON-NLS-1$
            skipRefs + ", skipTypeReferences=" + //$NON-NLS-1$
            skipTypeRefs + //$NON-NLS-1$
            ".");
            trace(//$NON-NLS-1$
            name + " Timings: " + (System.currentTimeMillis() - start) + //$NON-NLS-1$
            " total, " + fStatistics.fParsingTime + //$NON-NLS-1$
            " parser, " + fStatistics.fResolutionTime + //$NON-NLS-1$
            " resolution, " + fStatistics.fAddToIndexTime + //$NON-NLS-1$
            " index update.");
            int sum = fStatistics.fDeclarationCount + fStatistics.fReferenceCount + fStatistics.fProblemBindingCount;
            double problemPct = sum == 0 ? 0.0 : (double) fStatistics.fProblemBindingCount / (double) sum;
            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            trace(//$NON-NLS-1$
            name + " Result: " + fStatistics.fDeclarationCount + //$NON-NLS-1$
            " declarations, " + fStatistics.fReferenceCount + //$NON-NLS-1$
            " references, " + fStatistics.fErrorCount + //$NON-NLS-1$
            " errors, " + fStatistics.fProblemBindingCount + "(" + nf.format(problemPct) + //$NON-NLS-1$ //$NON-NLS-2$
            ") problems.");
            IWritableIndex index = fIndexer.getIndex();
            if (index != null) {
                long misses = index.getCacheMisses();
                long hits = index.getCacheHits();
                long tries = misses + hits;
                double missPct = tries == 0 ? 0.0 : (double) misses / (double) tries;
                trace(//$NON-NLS-1$
                name + " Cache: " + hits + //$NON-NLS-1$
                " hits, " + misses + "(" + nf.format(missPct) + //$NON-NLS-1$ //$NON-NLS-2$
                ") misses.");
            }
        }
    }

    @Override
    protected IStatus createStatus(String msg) {
        //$NON-NLS-1$
        return new Status(IStatus.ERROR, "org.eclipse.cdt.core", msg, null);
    }

    @Override
    protected IStatus createStatus(String msg, Throwable e) {
        //$NON-NLS-1$
        return new Status(IStatus.ERROR, "org.eclipse.cdt.core", msg, e);
    }

    @Override
    protected String getMessage(MessageKind kind, Object... arguments) {
        // Unfortunately we don't have OSGi on the remote system so for now we'll just settle for
        // English strings
        // TODO: find a way to do non-OSGi NLS
        switch(kind) {
            case parsingFileTask:
                //$NON-NLS-1$
                return MessageFormat.format("parsing {0} ({1})", arguments);
            case errorWhileParsing:
                //$NON-NLS-1$
                return MessageFormat.format("Error while parsing {0}.", arguments);
            case tooManyIndexProblems:
                //$NON-NLS-1$
                return "Too many errors while indexing, stopping indexer.";
        }
        return null;
    }

    @Override
    protected IParserLogService getLogService() {
        if (fLogger != null)
            return fLogger;
        return new StdoutLogService();
    }

    protected void setLogService(IParserLogService logService) {
        fLogger = logService;
    }

    @Override
    protected void logError(IStatus s) {
        trace(s.getMessage());
    }

    @Override
    protected void logException(Throwable e) {
        trace(e.getMessage());
    }

    @Override
    protected int[] getLinkagesToParse() {
        return IDS_FOR_LINKAGES_TO_INDEX;
    }

    @Override
    protected void trace(String message) {
        getLogService().traceLog(message);
    }
}

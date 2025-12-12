/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2009 IBM Corporation and others.
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

import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.core.index.Index;

public class RemoveFromIndex extends IndexRequest {

    public String resourceName;

    public RemoveFromIndex(String resourceName, IPath containerPath, IndexManager manager) {
        super(containerPath, manager);
        this.resourceName = resourceName;
    }

    @Override
    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
            return true;
        /* ensure no concurrent write access to index */
        Index index = this.manager.getIndex(this.containerPath, true, /*reuse index file*/
        false);
        if (index == null)
            return true;
        ReadWriteMonitor monitor = index.monitor;
        // index got deleted since acquired
        if (monitor == null)
            return true;
        try {
            // ask permission to write
            monitor.enterWrite();
            index.remove(this.resourceName);
        } finally {
            // free write lock
            monitor.exitWrite();
        }
        File indexFile = index.getIndexFile();
        if (indexFile != null) {
            this.manager.removeFromMetaIndex(index, indexFile, this.containerPath);
        }
        return true;
    }

    @Override
    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return "removing " + this.resourceName + " from index " + this.containerPath;
    }
}

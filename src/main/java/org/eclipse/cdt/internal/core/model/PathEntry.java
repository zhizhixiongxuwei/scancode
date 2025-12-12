/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2016 QNX Software Systems and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      QNX Software Systems - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.model;

import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class PathEntry implements IPathEntry {

    public int entryKind;

    public boolean isExported;

    public IPath path;

    public PathEntry(int entryKind, IPath path, boolean isExported) {
        this.path = (path == null) ? Path.EMPTY : path;
        this.entryKind = entryKind;
        this.isExported = isExported;
    }

    @Override
    public IPath getPath() {
        return path;
    }

    @Override
    public int getEntryKind() {
        return entryKind;
    }

    @Override
    public boolean isExported() {
        return isExported;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + entryKind;
        result = prime * result + (isExported ? 1231 : 1237);
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPathEntry) {
            IPathEntry otherEntry = (IPathEntry) obj;
            if (!path.equals(otherEntry.getPath())) {
                return false;
            }
            if (entryKind != otherEntry.getEntryKind()) {
                return false;
            }
            if (isExported != otherEntry.isExported()) {
                return false;
            }
            return true;
        }
        return super.equals(obj);
    }

    /**
     * Returns the kind from its <code>String</code> form.
     */
    public static int kindFromString(String kindStr) {
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("prj"))
            return IPathEntry.CDT_PROJECT;
        //if (kindStr.equalsIgnoreCase("var")) //$NON-NLS-1$
        //	return IPathEntry.CDT_VARIABLE;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("src"))
            return IPathEntry.CDT_SOURCE;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("lib"))
            return IPathEntry.CDT_LIBRARY;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("inc"))
            return IPathEntry.CDT_INCLUDE;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("incfile"))
            return IPathEntry.CDT_INCLUDE_FILE;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("mac"))
            return IPathEntry.CDT_MACRO;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("macfile"))
            return IPathEntry.CDT_MACRO_FILE;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("con"))
            return IPathEntry.CDT_CONTAINER;
        if (//$NON-NLS-1$
        kindStr.equalsIgnoreCase("out"))
            return IPathEntry.CDT_OUTPUT;
        return -1;
    }

    /**
     * Returns a <code>String</code> for the kind of a path entry.
     */
    static String kindToString(int kind) {
        switch(kind) {
            case IPathEntry.CDT_PROJECT:
                //$NON-NLS-1$
                return "prj";
            case IPathEntry.CDT_SOURCE:
                //$NON-NLS-1$
                return "src";
            case IPathEntry.CDT_LIBRARY:
                //$NON-NLS-1$
                return "lib";
            case IPathEntry.CDT_INCLUDE:
                //$NON-NLS-1$
                return "inc";
            case IPathEntry.CDT_INCLUDE_FILE:
                //$NON-NLS-1$
                return "incfile";
            case IPathEntry.CDT_MACRO:
                //$NON-NLS-1$
                return "mac";
            case IPathEntry.CDT_MACRO_FILE:
                //$NON-NLS-1$
                return "macfile";
            case IPathEntry.CDT_CONTAINER:
                //$NON-NLS-1$
                return "con";
            case IPathEntry.CDT_OUTPUT:
                //$NON-NLS-1$
                return "out";
            default:
                //$NON-NLS-1$
                return "unknown";
        }
    }

    /**
     * Returns a printable representation of this classpath entry.
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (path != null && !path.isEmpty()) {
            buffer.append(path.toString()).append(' ');
        }
        buffer.append('[');
        buffer.append(getKindString());
        buffer.append(']');
        return buffer.toString();
    }

    String getKindString() {
        switch(getEntryKind()) {
            case IPathEntry.CDT_LIBRARY:
                //$NON-NLS-1$
                return ("Library path");
            case IPathEntry.CDT_PROJECT:
                //$NON-NLS-1$
                return ("Project path");
            case IPathEntry.CDT_SOURCE:
                //$NON-NLS-1$
                return ("Source path");
            case IPathEntry.CDT_OUTPUT:
                //$NON-NLS-1$
                return ("Output path");
            case IPathEntry.CDT_INCLUDE:
                //$NON-NLS-1$
                return ("Include path");
            case IPathEntry.CDT_INCLUDE_FILE:
                //$NON-NLS-1$
                return ("Include-file path");
            case IPathEntry.CDT_MACRO:
                //$NON-NLS-1$
                return ("Symbol definition");
            case IPathEntry.CDT_MACRO_FILE:
                //$NON-NLS-1$
                return ("Symbol-file definition");
            case IPathEntry.CDT_CONTAINER:
                //$NON-NLS-1$
                return ("Contributed paths");
        }
        //$NON-NLS-1$
        return ("Unknown");
    }
}

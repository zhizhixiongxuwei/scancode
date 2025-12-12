/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
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
package org.eclipse.jdt.internal.core.jdom;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

/**
 * Implements a very simple version of the ICompilationUnit.
 *
 * <p>Please do not use outside of jdom.</p>
 */
public class CompilationUnit implements ICompilationUnit {

    public char[] fContents;

    public char[] fFileName;

    public char[] fMainTypeName;

    public CompilationUnit(char[] contents, char[] filename) {
        this.fContents = contents;
        this.fFileName = filename;
        String file = new String(filename);
        //$NON-NLS-1$
        int start = file.lastIndexOf("/") + 1;
        if (//$NON-NLS-1$
        start == 0 || start < file.lastIndexOf("\\"))
            //$NON-NLS-1$
            start = file.lastIndexOf("\\") + 1;
        //$NON-NLS-1$
        int end = file.lastIndexOf(".");
        if (end == -1)
            end = file.length();
        this.fMainTypeName = file.substring(start, end).toCharArray();
    }

    @Override
    public char[] getContents() {
        return this.fContents;
    }

    /**
     * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
     */
    @Override
    public char[] getFileName() {
        return this.fFileName;
    }

    @Override
    public char[] getMainTypeName() {
        return this.fMainTypeName;
    }

    @Override
    public char[][] getPackageName() {
        return null;
    }

    @Override
    public boolean ignoreOptionalProblems() {
        return false;
    }

    @Override
    public String toString() {
        //$NON-NLS-2$ //$NON-NLS-1$
        return "CompilationUnit[" + new String(this.fFileName) + "]";
    }

    @Override
    public char[] getModuleName() {
        // TODO Java 9 Auto-generated method stub
        return null;
    }
}

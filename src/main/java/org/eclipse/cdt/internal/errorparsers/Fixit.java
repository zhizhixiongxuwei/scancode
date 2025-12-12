/**
 * ****************************************************************************
 *  Copyright (c) 2017 Red Hat Inc. and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      Red Hat Inc. - initial API
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.errorparsers;

public class Fixit {

    public String change;

    public int lineNumber;

    public int columnNumber;

    public int length;

    public Fixit(String range, String change) {
        this.change = change;
        parseRange(range);
    }

    private void parseRange(String range) {
        //$NON-NLS-1$
        String[] region = range.split("-");
        String start = region[0];
        //$NON-NLS-1$
        String[] token = start.split(":");
        this.lineNumber = Integer.valueOf(token[0]).intValue();
        this.columnNumber = Integer.valueOf(token[1]).intValue();
        String end = region[1];
        //$NON-NLS-1$
        token = end.split(":");
        int endColumnNumber = Integer.valueOf(token[1]).intValue();
        this.length = endColumnNumber - columnNumber;
    }

    /**
     * Get line number.
     *
     * @return 1-based line number of fix-it
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get column number.
     *
     * @return 1-based column number of fix-it
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Get length.
     *
     * @return length of change for fix-it
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the change string.
     * @return the string to change the region to (can be empty).
     */
    public String getChange() {
        return change;
    }
}

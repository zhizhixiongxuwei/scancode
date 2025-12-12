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
 *      QNX Software Systems - Initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.model;

import org.eclipse.cdt.core.model.ISourceRange;

/**
 * @see ISourceRange
 */
public class SourceRange implements ISourceRange {

    protected int startPos, length;

    protected int idStartPos, idLength;

    protected int startLine, endLine;

    protected SourceRange(int startPos, int length) {
        this.startPos = startPos;
        this.length = length;
        idStartPos = 0;
        idLength = 0;
        startLine = 0;
        endLine = 0;
    }

    protected SourceRange(int startPos, int length, int idStartPos, int idLength) {
        this.startPos = startPos;
        this.length = length;
        this.idStartPos = idStartPos;
        this.idLength = idLength;
    }

    protected SourceRange(int startPos, int length, int idStartPos, int idLength, int startLine, int endLine) {
        this.startPos = startPos;
        this.length = length;
        this.idStartPos = idStartPos;
        this.idLength = idLength;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    /**
     * @see ISourceRange
     */
    @Override
    public int getLength() {
        return length;
    }

    /**
     * @see ISourceRange
     */
    @Override
    public int getStartPos() {
        return startPos;
    }

    /**
     */
    @Override
    public int getIdStartPos() {
        return idStartPos;
    }

    @Override
    public int getIdLength() {
        return idLength;
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        //$NON-NLS-1$
        buffer.append("[offset=");
        buffer.append(this.startPos);
        //$NON-NLS-1$
        buffer.append(", length=");
        buffer.append(this.length);
        //$NON-NLS-1$
        buffer.append("]");
        //$NON-NLS-1$
        buffer.append("[IdOffset=");
        buffer.append(this.idStartPos);
        //$NON-NLS-1$
        buffer.append(", idLength=");
        buffer.append(this.idLength);
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }
}

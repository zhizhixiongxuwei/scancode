package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import java.util.Objects;

public class ASTFileLocation implements IASTFileLocation {

    public LocationCtxFile fLocationCtx;

    public int fOffset;

    public int fLength;

    public ASTFileLocation(LocationCtxFile fileLocationCtx, int startOffset, int length) {
        fLocationCtx = fileLocationCtx;
        fOffset = startOffset;
        fLength = length;
    }

    @Override
    public String getFileName() {
        return fLocationCtx.getFilePath();
    }

    @Override
    public IASTFileLocation asFileLocation() {
        return this;
    }

    @Override
    public int getNodeLength() {
        return fLength;
    }

    @Override
    public int getNodeOffset() {
        return fOffset;
    }

    @Override
    public int getEndingLineNumber() {
        int end = fLength > 0 ? fOffset + fLength - 1 : fOffset;
        return fLocationCtx.getLineNumber(end);
    }

    @Override
    public int getStartingLineNumber() {
        return fLocationCtx.getLineNumber(fOffset);
    }

    public char[] getSource() {
        return fLocationCtx.getSource(fOffset, fLength);
    }

    @Override
    public String toString() {
        //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        return getFileName() + "[" + fOffset + "," + (fOffset + fLength) + "]";
    }

    public int getSequenceNumber() {
        return fLocationCtx.getSequenceNumberForOffset(fOffset, true);
    }

    public int getSequenceEndNumber() {
        return fLocationCtx.getSequenceNumberForOffset(fOffset + fLength, true);
    }

    public LocationCtxFile getLocationContext() {
        return fLocationCtx;
    }

    @Override
    public IASTPreprocessorIncludeStatement getContextInclusionStatement() {
        return fLocationCtx.getInclusionStatement();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ASTFileLocation other = (ASTFileLocation) obj;
        if (fOffset != other.fOffset)
            return false;
        if (fLength != other.fLength)
            return false;
        return Objects.equals(fLocationCtx, other.fLocationCtx);
    }
}

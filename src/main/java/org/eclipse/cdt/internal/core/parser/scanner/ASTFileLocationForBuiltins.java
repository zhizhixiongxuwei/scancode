package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;

public class ASTFileLocationForBuiltins implements IASTFileLocation {

    public String fFile;

    public int fOffset;

    public int fLength;

    public ASTFileLocationForBuiltins(String file, int startOffset, int length) {
        fFile = file;
        fOffset = startOffset;
        fLength = length;
    }

    @Override
    public String getFileName() {
        return fFile;
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
        return 0;
    }

    @Override
    public int getStartingLineNumber() {
        return 0;
    }

    @Override
    public IASTPreprocessorIncludeStatement getContextInclusionStatement() {
        return null;
    }
}

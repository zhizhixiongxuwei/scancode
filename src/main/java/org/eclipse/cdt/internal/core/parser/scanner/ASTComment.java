package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTComment extends ASTPreprocessorNode implements IASTComment {

    final public boolean fIsBlockComment;

    public String fFilePath;

    public ASTComment(IASTTranslationUnit parent, String filePath, int offset, int endOffset, boolean isBlockComment) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, offset, endOffset);
        fIsBlockComment = isBlockComment;
        fFilePath = filePath;
    }

    @Override
    public int getOffset() {
        if (fFilePath != null) {
            // Perform lazy conversion to sequence number.
            ILocationResolver lr = getTranslationUnit().getAdapter(ILocationResolver.class);
            if (lr != null) {
                setOffset(lr.getSequenceNumberForFileOffset(fFilePath, super.getOffset()));
                fFilePath = null;
            }
        }
        return super.getOffset();
    }

    @Override
    public char[] getComment() {
        return getRawSignatureChars();
    }

    @Override
    public boolean isBlockComment() {
        return fIsBlockComment;
    }

    @Override
    public void setComment(char[] comment) {
        assert false;
    }
}

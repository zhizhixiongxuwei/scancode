package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorErrorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTError extends ASTDirectiveWithCondition implements IASTPreprocessorErrorStatement {

    public ASTError(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber) {
        super(parent, startNumber, condNumber, condEndNumber, true);
    }

    @Override
    public char[] getMessage() {
        return getCondition();
    }
}

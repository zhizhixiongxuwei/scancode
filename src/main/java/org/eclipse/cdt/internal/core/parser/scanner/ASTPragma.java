package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorPragmaStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTPragma extends ASTDirectiveWithCondition implements IASTPreprocessorPragmaStatement {

    public ASTPragma(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber) {
        super(parent, startNumber, condNumber, condEndNumber, true);
    }

    @Override
    public char[] getMessage() {
        return getCondition();
    }

    @Override
    public boolean isPragmaOperator() {
        return false;
    }
}

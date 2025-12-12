package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTElse extends ASTPreprocessorNode implements IASTPreprocessorElseStatement {

    private final boolean fTaken;

    public ASTElse(IASTTranslationUnit parent, int startNumber, int endNumber, boolean taken) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fTaken = taken;
    }

    @Override
    public boolean taken() {
        return fTaken;
    }
}

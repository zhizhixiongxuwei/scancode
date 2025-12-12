package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTEndif extends ASTPreprocessorNode implements IASTPreprocessorEndifStatement {

    public ASTEndif(IASTTranslationUnit parent, int startNumber, int endNumber) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
    }
}
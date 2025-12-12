package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTIf extends ASTDirectiveWithCondition implements IASTPreprocessorIfStatement {

    public ASTIf(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber, boolean taken) {
        super(parent, startNumber, condNumber, condEndNumber, taken);
    }
}
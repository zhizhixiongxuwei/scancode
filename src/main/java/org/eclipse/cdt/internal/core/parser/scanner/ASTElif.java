package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTElif extends ASTDirectiveWithCondition implements IASTPreprocessorElifStatement {

    public ASTElif(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber, boolean taken) {
        super(parent, startNumber, condNumber, condEndNumber, taken);
    }
}
package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorUndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;

public class ASTUndef extends ASTPreprocessorNode implements IASTPreprocessorUndefStatement {

    private final ASTPreprocessorName fName;

    public ASTUndef(IASTTranslationUnit parent, char[] name, int startNumber, int nameNumber, int nameEndNumber, IBinding binding, boolean isActive) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, nameEndNumber);
        fName = new ASTPreprocessorName(this, IASTPreprocessorStatement.MACRO_NAME, nameNumber, nameEndNumber, name, binding);
        if (!isActive)
            setInactive();
    }

    @Override
    public ASTPreprocessorName getMacroName() {
        return fName;
    }
}

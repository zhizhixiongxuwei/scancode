package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;

public class ASTIfdef extends ASTDirectiveWithCondition implements IASTPreprocessorIfdefStatement {

    private ASTMacroReferenceName fMacroRef;

    public ASTIfdef(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber, boolean taken, IMacroBinding macro) {
        super(parent, startNumber, condNumber, condEndNumber, taken);
        if (macro != null) {
            fMacroRef = new ASTMacroReferenceName(this, IASTPreprocessorStatement.MACRO_NAME, condNumber, condEndNumber, macro, null);
        }
    }

    @Override
    public ASTPreprocessorName getMacroReference() {
        return fMacroRef;
    }
}

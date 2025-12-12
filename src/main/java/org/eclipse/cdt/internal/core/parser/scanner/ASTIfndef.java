package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;

public class ASTIfndef extends ASTDirectiveWithCondition implements IASTPreprocessorIfndefStatement {

    private ASTMacroReferenceName fMacroRef;

    public ASTIfndef(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber, boolean taken, IMacroBinding macro) {
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

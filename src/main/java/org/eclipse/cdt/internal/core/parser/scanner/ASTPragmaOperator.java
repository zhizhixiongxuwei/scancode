package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTPragmaOperator extends ASTPragma {

    private final int fConditionEndOffset;

    public ASTPragmaOperator(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber, int endNumber) {
        super(parent, startNumber, condNumber, endNumber);
        fConditionEndOffset = condEndNumber;
    }

    @Override
    public String getConditionString() {
        return new String(getSource(fConditionOffset, fConditionEndOffset));
    }

    @Override
    public boolean isPragmaOperator() {
        return true;
    }
}

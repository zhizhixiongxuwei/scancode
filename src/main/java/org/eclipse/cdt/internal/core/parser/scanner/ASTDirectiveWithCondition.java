package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

abstract public class ASTDirectiveWithCondition extends ASTPreprocessorNode {

    protected final int fConditionOffset;

    private final boolean fTaken;

    public ASTDirectiveWithCondition(IASTTranslationUnit parent, int startNumber, int condNumber, int endNumber, boolean taken) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fConditionOffset = condNumber;
        fTaken = taken;
    }

    public boolean taken() {
        return fTaken;
    }

    public String getConditionString() {
        return new String(getSource(fConditionOffset, getOffset() + getLength() - fConditionOffset));
    }

    public char[] getCondition() {
        return getConditionString().toCharArray();
    }
}

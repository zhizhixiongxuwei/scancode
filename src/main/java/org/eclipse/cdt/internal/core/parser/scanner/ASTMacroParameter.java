package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;

public class ASTMacroParameter extends ASTPreprocessorNode implements IASTFunctionStyleMacroParameter {

    private final String fParameter;

    public ASTMacroParameter(IASTPreprocessorFunctionStyleMacroDefinition parent, char[] param, int offset, int endOffset) {
        super(parent, IASTPreprocessorFunctionStyleMacroDefinition.PARAMETER, offset, endOffset);
        fParameter = new String(param);
    }

    @Override
    public String getParameter() {
        return fParameter;
    }

    @Override
    public void setParameter(String value) {
        assert false;
    }
}

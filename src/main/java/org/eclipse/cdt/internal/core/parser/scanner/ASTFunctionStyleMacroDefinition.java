package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;

public class ASTFunctionStyleMacroDefinition extends ASTMacroDefinition implements IASTPreprocessorFunctionStyleMacroDefinition {

    /**
     * Regular constructor.
     */
    public ASTFunctionStyleMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, int startNumber, int nameNumber, int nameEndNumber, int expansionNumber, int endNumber, boolean active) {
        super(parent, macro, startNumber, nameNumber, nameEndNumber, expansionNumber, endNumber, active);
    }

    /**
     * Constructor for builtins
     */
    public ASTFunctionStyleMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, IName originalDefinition, int expansionOffset) {
        super(parent, macro, originalDefinition, expansionOffset);
    }

    @Override
    public IASTFunctionStyleMacroParameter[] getParameters() {
        IMacroBinding macro = getMacro();
        char[][] paramList = macro.getParameterList();
        IASTFunctionStyleMacroParameter[] result = new IASTFunctionStyleMacroParameter[paramList.length];
        char[] image = getRawSignatureChars();
        int idx = 0;
        int defOffset = getOffset();
        int endIdx = Math.min(fExpansionNumber - defOffset, image.length);
        char start = '(';
        for (int i = 0; i < result.length; i++) {
            while (idx < endIdx && image[idx] != start) idx++;
            idx++;
            while (idx < endIdx && Character.isWhitespace(image[idx])) idx++;
            start = ',';
            char[] param = paramList[i];
            int poffset = -1;
            int pendOffset = -1;
            if (idx + param.length <= endIdx) {
                poffset = defOffset + idx;
                pendOffset = poffset + param.length;
            }
            result[i] = new ASTMacroParameter(this, param, poffset, pendOffset);
        }
        return result;
    }

    @Override
    public void addParameter(IASTFunctionStyleMacroParameter parm) {
        assert false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getName().getSimpleID());
        result.append('(');
        boolean needComma = false;
        for (IASTFunctionStyleMacroParameter param : getParameters()) {
            if (needComma) {
                result.append(',');
            }
            result.append(param.getParameter());
            needComma = true;
        }
        result.append(')');
        result.append('=');
        result.append(getExpansion());
        return result.toString();
    }
}

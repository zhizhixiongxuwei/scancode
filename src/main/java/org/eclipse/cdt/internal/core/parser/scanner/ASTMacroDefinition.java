package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ASTNodeSpecification;

public class ASTMacroDefinition extends ASTPreprocessorNode implements IASTPreprocessorObjectStyleMacroDefinition {

    private final ASTPreprocessorName fName;

    protected final int fExpansionNumber;

    private final int fExpansionOffset;

    /**
     * Regular constructor.
     */
    public ASTMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, int startNumber, int nameNumber, int nameEndNumber, int expansionNumber, int endNumber, boolean active) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fExpansionNumber = expansionNumber;
        fExpansionOffset = -1;
        fName = new ASTPreprocessorDefinition(this, IASTPreprocessorMacroDefinition.MACRO_NAME, nameNumber, nameEndNumber, macro.getNameCharArray(), macro);
        if (!active)
            setInactive();
    }

    /**
     * Constructor for built-in macros
     * @param expansionOffset
     */
    public ASTMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, IName originalDefinition, int expansionOffset) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, -1, -1);
        fName = new ASTBuiltinName(this, IASTPreprocessorMacroDefinition.MACRO_NAME, originalDefinition, macro.getNameCharArray(), macro);
        fExpansionNumber = -1;
        fExpansionOffset = expansionOffset;
    }

    @Override
    public String getContainingFilename() {
        if (fName instanceof ASTBuiltinName) {
            return fName.getContainingFilename();
        }
        return super.getContainingFilename();
    }

    protected IMacroBinding getMacro() {
        return (IMacroBinding) fName.getBinding();
    }

    @Override
    public String getExpansion() {
        return new String(getMacro().getExpansion());
    }

    @Override
    public IASTName getName() {
        return fName;
    }

    @Override
    public int getRoleForName(IASTName n) {
        return fName == n ? r_definition : r_unclear;
    }

    @Override
    void findNode(ASTNodeSpecification<?> nodeSpec) {
        super.findNode(nodeSpec);
        nodeSpec.visit(fName);
    }

    @Override
    public IASTFileLocation getExpansionLocation() {
        if (fExpansionNumber >= 0) {
            IASTTranslationUnit ast = getTranslationUnit();
            if (ast != null) {
                ILocationResolver lr = ast.getAdapter(ILocationResolver.class);
                if (lr != null) {
                    return lr.getMappedFileLocation(fExpansionNumber, getOffset() + getLength() - fExpansionNumber);
                }
            }
        }
        if (fExpansionOffset >= 0) {
            String fileName = fName.getContainingFilename();
            if (fileName != null) {
                final char[] expansionImage = getMacro().getExpansionImage();
                return new ASTFileLocationForBuiltins(fileName, fExpansionOffset, expansionImage.length);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName().toString() + '=' + getExpansion();
    }
}

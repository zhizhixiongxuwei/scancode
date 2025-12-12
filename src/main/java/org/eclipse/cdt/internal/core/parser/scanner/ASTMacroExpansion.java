package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class ASTMacroExpansion extends ASTPreprocessorNode implements IASTPreprocessorMacroExpansion {

    private LocationCtxMacroExpansion fContext;

    public ASTMacroExpansion(IASTNode parent, int startNumber, int endNumber) {
        super(parent, IASTTranslationUnit.MACRO_EXPANSION, startNumber, endNumber);
    }

    void setContext(LocationCtxMacroExpansion expansionCtx) {
        fContext = expansionCtx;
    }

    @Override
    public ASTMacroReferenceName getMacroReference() {
        return fContext.getMacroReference();
    }

    @Override
    public IASTPreprocessorMacroDefinition getMacroDefinition() {
        return fContext.getMacroDefinition();
    }

    @Override
    public ASTPreprocessorName[] getNestedMacroReferences() {
        return fContext.getNestedMacroReferences();
    }

    public LocationCtxMacroExpansion getContext() {
        return fContext;
    }
}

package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

public class ASTPreprocessorDefinition extends ASTPreprocessorName {

    public ASTPreprocessorDefinition(IASTNode parent, ASTNodeProperty property, int startNumber, int endNumber, char[] name, IBinding binding) {
        super(parent, property, startNumber, endNumber, name, binding);
    }

    @Override
    public boolean isDefinition() {
        return true;
    }

    @Override
    public int getRoleOfName(boolean allowResolution) {
        return IASTNameOwner.r_definition;
    }
}

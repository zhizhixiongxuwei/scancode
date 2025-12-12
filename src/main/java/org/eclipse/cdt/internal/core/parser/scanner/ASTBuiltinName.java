package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.core.runtime.IAdaptable;

public class ASTBuiltinName extends ASTPreprocessorDefinition implements IAdaptable {

    final public IName fOriginalDefinition;

    public ASTBuiltinName(IASTNode parent, ASTNodeProperty property, IName originalDefinition, char[] name, IBinding binding) {
        super(parent, property, -1, -1, name, binding);
        fOriginalDefinition = originalDefinition;
    }

    @Override
    public boolean contains(IASTNode node) {
        return node == this;
    }

    @Override
    public String getContainingFilename() {
        IASTFileLocation fileLocation = getFileLocation();
        //$NON-NLS-1$
        return fileLocation == null ? "" : fileLocation.getFileName();
    }

    @Override
    public IASTFileLocation getFileLocation() {
        return fOriginalDefinition == null ? null : fOriginalDefinition.getFileLocation();
    }

    @Override
    public IASTNodeLocation[] getNodeLocations() {
        IASTFileLocation fileLocation = getFileLocation();
        if (fileLocation == null) {
            return IASTNodeLocation.EMPTY_ARRAY;
        }
        return new IASTNodeLocation[] { fileLocation };
    }

    @Override
    public String getRawSignature() {
        IASTFileLocation fileLocation = getFileLocation();
        //$NON-NLS-1$
        return fileLocation == null ? "" : toString();
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.isAssignableFrom(ASTBuiltinName.class)) {
            return adapter.cast(this);
        }
        if (fOriginalDefinition != null && adapter.isAssignableFrom(fOriginalDefinition.getClass())) {
            return adapter.cast(fOriginalDefinition);
        }
        return null;
    }
}

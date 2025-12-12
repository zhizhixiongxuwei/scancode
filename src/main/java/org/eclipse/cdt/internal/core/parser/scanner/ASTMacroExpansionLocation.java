package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.*;

@SuppressWarnings("deprecation")
public class ASTMacroExpansionLocation implements IASTMacroExpansionLocation, org.eclipse.cdt.core.dom.ast.IASTMacroExpansion {

    private LocationCtxMacroExpansion fContext;

    private int fOffset;

    private int fLength;

    public ASTMacroExpansionLocation(LocationCtxMacroExpansion macroExpansionCtx, int offset, int length) {
        fContext = macroExpansionCtx;
        fOffset = offset;
        fLength = length;
    }

    @Override
    public IASTPreprocessorMacroExpansion getExpansion() {
        return fContext.getExpansion();
    }

    @Override
    public IASTNodeLocation[] getExpansionLocations() {
        final IASTFileLocation fl = asFileLocation();
        return fl == null ? new IASTNodeLocation[0] : new IASTNodeLocation[] { fl };
    }

    @Override
    public IASTPreprocessorMacroDefinition getMacroDefinition() {
        return fContext.getMacroDefinition();
    }

    @Override
    public IASTName getMacroReference() {
        return fContext.getMacroReference();
    }

    @Override
    public IASTFileLocation asFileLocation() {
        return ((LocationCtxContainer) fContext.getParent()).createFileLocation(fContext.fOffsetInParent, fContext.fEndOffsetInParent - fContext.fOffsetInParent);
    }

    @Override
    public int getNodeLength() {
        return fLength;
    }

    @Override
    public int getNodeOffset() {
        return fOffset;
    }

    @Override
    public String toString() {
        //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        return fContext.getMacroDefinition().getName().toString() + "[" + fOffset + "," + (fOffset + fLength) + "]";
    }

    public IASTImageLocation getImageLocation() {
        return fContext.getImageLocation(fOffset, fLength);
    }
}

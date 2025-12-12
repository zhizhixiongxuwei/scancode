package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.*;

public class ASTMacroReferenceName extends ASTPreprocessorName {

    public ImageLocationInfo fImageLocationInfo;

    public ASTMacroReferenceName(IASTNode parent, ASTNodeProperty property, int offset, int endOffset, IMacroBinding macro, ImageLocationInfo imgLocationInfo) {
        super(parent, property, offset, endOffset, macro.getNameCharArray(), macro);
        fImageLocationInfo = imgLocationInfo;
    }

    @Override
    public int getRoleOfName(boolean allowResolution) {
        return IASTNameOwner.r_unclear;
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public IASTImageLocation getImageLocation() {
        if (fImageLocationInfo != null) {
            IASTTranslationUnit tu = getTranslationUnit();
            if (tu != null) {
                LocationMap lr = tu.getAdapter(LocationMap.class);
                if (lr != null) {
                    return fImageLocationInfo.createLocation(lr, fImageLocationInfo);
                }
            }
            return null;
        }
        // ASTNode.getImageLocation() computes an image location based on the node location.
        // Macro reference names which are nested references rather than the name of the
        // macro being expanded itself, have their node location set to the entire macro
        // expansion (see LocationMap.pushMacroExpansion()), which doesn't produce a
        // useful image location.
        if (getParent() instanceof ASTMacroExpansion) {
            if (((ASTMacroExpansion) getParent()).getContext().getMacroReference() == this) {
                return super.getImageLocation();
            }
        }
        return null;
    }
}

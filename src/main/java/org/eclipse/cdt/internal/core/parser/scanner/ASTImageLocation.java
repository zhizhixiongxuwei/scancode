package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTImageLocation;

public class ASTImageLocation extends ASTFileLocationForBuiltins implements IASTImageLocation {

    private final int fKind;

    public ASTImageLocation(int kind, String file, int offset, int length) {
        super(file, offset, length);
        fKind = kind;
    }

    @Override
    public int getLocationKind() {
        return fKind;
    }
}

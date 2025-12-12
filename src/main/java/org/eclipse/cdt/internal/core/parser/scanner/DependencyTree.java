package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class DependencyTree extends ASTInclusionNode implements IASTTranslationUnit.IDependencyTree {

    public DependencyTree(LocationCtx ctx) {
        super(ctx);
    }

    @Override
    public IASTInclusionNode[] getInclusions() {
        return getNestedInclusions();
    }

    @Override
    public String getTranslationUnitPath() {
        return fLocationCtx.getFilePath();
    }
}

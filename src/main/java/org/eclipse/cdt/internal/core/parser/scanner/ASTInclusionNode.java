package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.ArrayList;

public class ASTInclusionNode implements IASTTranslationUnit.IDependencyTree.IASTInclusionNode {

    protected LocationCtx fLocationCtx;

    private IASTTranslationUnit.IDependencyTree.IASTInclusionNode[] fInclusions;

    public ASTInclusionNode(LocationCtx ctx) {
        fLocationCtx = ctx;
    }

    @Override
    public IASTPreprocessorIncludeStatement getIncludeDirective() {
        return fLocationCtx.getInclusionStatement();
    }

    @Override
    public IASTTranslationUnit.IDependencyTree.IASTInclusionNode[] getNestedInclusions() {
        if (fInclusions == null) {
            ArrayList<IASTTranslationUnit.IDependencyTree.IASTInclusionNode> result = new ArrayList<>();
            fLocationCtx.getInclusions(result);
            fInclusions = result.toArray(new IASTTranslationUnit.IDependencyTree.IASTInclusionNode[result.size()]);
        }
        return fInclusions;
    }
}

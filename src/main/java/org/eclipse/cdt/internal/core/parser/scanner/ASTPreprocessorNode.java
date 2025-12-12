/**
 * ****************************************************************************
 *  Copyright (c) 2007, 2015 Wind River Systems, Inc. and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      Markus Schorn - initial API and implementation
 *      Sergey Prigogin (Google)
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.parser.scanner;

import java.util.ArrayList;
import java.util.Objects;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorErrorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorPragmaStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorUndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFileNomination;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.parser.ISignificantMacros;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNodeSpecification;
import org.eclipse.core.runtime.CoreException;

/**
 * Models various AST-constructs obtained from the preprocessor.
 * @since 5.0
 */
abstract public class ASTPreprocessorNode extends ASTNode {

    public ASTPreprocessorNode(IASTNode parent, ASTNodeProperty property, int startNumber, int endNumber) {
        setParent(parent);
        setPropertyInParent(property);
        setOffset(startNumber);
        setLength(endNumber - startNumber);
    }

    protected char[] getSource(int offset, int length) {
        final IASTTranslationUnit ast = getTranslationUnit();
        if (ast != null) {
            ILocationResolver lr = ast.getAdapter(ILocationResolver.class);
            if (lr != null) {
                final IASTFileLocation loc = lr.getMappedFileLocation(offset, length);
                if (loc != null) {
                    return lr.getUnpreprocessedSignature(loc);
                }
            }
        }
        return CharArrayUtils.EMPTY;
    }

    /**
     * Searches nodes by file location.
     */
    void findNode(ASTNodeSpecification<?> nodeSpec) {
        nodeSpec.visit(this);
    }

    @Override
    public IASTNode copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IASTNode copy(CopyStyle style) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IToken getLeadingSyntax() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IToken getTrailingSyntax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.valueOf(getRawSignatureChars());
    }
}

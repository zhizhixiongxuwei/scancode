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
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.parser.scanner;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Models IASTNames as needed for the preprocessor statements and macro expansions.
 * @since 5.0
 */
public class ASTPreprocessorName extends ASTPreprocessorNode implements IASTName {

    final public char[] fName;

    final public IBinding fBinding;

    public ASTPreprocessorName(IASTNode parent, ASTNodeProperty property, int startNumber, int endNumber, char[] name, IBinding binding) {
        super(parent, property, startNumber, endNumber);
        fName = name;
        fBinding = binding;
    }

    @Override
    public IBinding resolveBinding() {
        return fBinding;
    }

    @Override
    public IBinding resolvePreBinding() {
        return fBinding;
    }

    @Override
    public IBinding getBinding() {
        return fBinding;
    }

    @Override
    public IBinding getPreBinding() {
        return fBinding;
    }

    @Override
    public ILinkage getLinkage() {
        final IASTTranslationUnit tu = getTranslationUnit();
        return tu == null ? Linkage.NO_LINKAGE : tu.getLinkage();
    }

    @Override
    public IASTCompletionContext getCompletionContext() {
        return null;
    }

    @Override
    public boolean isDeclaration() {
        return false;
    }

    @Override
    public boolean isDefinition() {
        return false;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public char[] toCharArray() {
        return fName;
    }

    @Override
    public char[] getSimpleID() {
        return fName;
    }

    @Override
    public char[] getLookupKey() {
        return fName;
    }

    @Override
    public String toString() {
        return new String(fName);
    }

    @Override
    public void setBinding(IBinding binding) {
        assert false;
    }

    @Override
    public int getRoleOfName(boolean allowResolution) {
        return IASTNameOwner.r_unclear;
    }

    @Override
    public IASTName getLastName() {
        return this;
    }

    @Override
    public boolean isQualified() {
        return false;
    }

    @Override
    public IASTName copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IASTName copy(CopyStyle style) {
        throw new UnsupportedOperationException();
    }
}

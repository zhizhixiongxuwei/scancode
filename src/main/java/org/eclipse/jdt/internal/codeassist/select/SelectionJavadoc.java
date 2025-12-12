/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2011 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.jdt.internal.codeassist.select;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

/**
 * Node representing a Javadoc comment including code selection.
 */
public class SelectionJavadoc extends Javadoc {

    public Expression selectedNode;

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=171019
    // Flag raised when selection is done on inheritDoc javadoc tag
    public boolean inheritDocSelected;

    public SelectionJavadoc(int sourceStart, int sourceEnd) {
        super(sourceStart, sourceEnd);
        this.inheritDocSelected = false;
    }

    @Override
    public StringBuilder print(int indent, StringBuilder output) {
        super.print(indent, output);
        if (this.selectedNode != null) {
            String selectedString = null;
            if (this.selectedNode instanceof JavadocFieldReference) {
                JavadocFieldReference fieldRef = (JavadocFieldReference) this.selectedNode;
                if (fieldRef.methodBinding != null) {
                    //$NON-NLS-1$
                    selectedString = "<SelectOnMethod:";
                } else {
                    //$NON-NLS-1$
                    selectedString = "<SelectOnField:";
                }
            } else if (this.selectedNode instanceof JavadocMessageSend) {
                //$NON-NLS-1$
                selectedString = "<SelectOnMethod:";
            } else if (this.selectedNode instanceof JavadocAllocationExpression) {
                //$NON-NLS-1$
                selectedString = "<SelectOnConstructor:";
            } else if (this.selectedNode instanceof JavadocSingleNameReference) {
                //$NON-NLS-1$
                selectedString = "<SelectOnLocalVariable:";
            } else if (this.selectedNode instanceof JavadocSingleTypeReference) {
                JavadocSingleTypeReference typeRef = (JavadocSingleTypeReference) this.selectedNode;
                if (typeRef.packageBinding == null) {
                    //$NON-NLS-1$
                    selectedString = "<SelectOnType:";
                }
            } else if (this.selectedNode instanceof JavadocQualifiedTypeReference) {
                JavadocQualifiedTypeReference typeRef = (JavadocQualifiedTypeReference) this.selectedNode;
                if (typeRef.packageBinding == null) {
                    //$NON-NLS-1$
                    selectedString = "<SelectOnType:";
                }
            } else {
                //$NON-NLS-1$
                selectedString = "<SelectOnType:";
            }
            int pos = output.length() - 3;
            output.replace(pos - 2, pos, selectedString + this.selectedNode + '>');
        }
        return output;
    }

    /**
     * Resolve selected node if not null and throw exception to let clients know
     * that it has been found.
     */
    private void internalResolve(Scope scope) {
        if (this.selectedNode != null) {
            switch(scope.kind) {
                case Scope.CLASS_SCOPE:
                    this.selectedNode.resolveType((ClassScope) scope);
                    break;
                case Scope.METHOD_SCOPE:
                    this.selectedNode.resolveType((MethodScope) scope);
                    break;
            }
            Binding binding = null;
            if (this.selectedNode instanceof JavadocFieldReference) {
                JavadocFieldReference fieldRef = (JavadocFieldReference) this.selectedNode;
                binding = fieldRef.binding;
                if (binding == null && fieldRef.methodBinding != null) {
                    binding = fieldRef.methodBinding;
                }
            } else if (this.selectedNode instanceof JavadocMessageSend) {
                binding = ((JavadocMessageSend) this.selectedNode).binding;
            } else if (this.selectedNode instanceof JavadocAllocationExpression) {
                binding = ((JavadocAllocationExpression) this.selectedNode).binding;
            } else if (this.selectedNode instanceof JavadocSingleNameReference) {
                binding = ((JavadocSingleNameReference) this.selectedNode).binding;
            } else if (this.selectedNode instanceof JavadocSingleTypeReference) {
                JavadocSingleTypeReference typeRef = (JavadocSingleTypeReference) this.selectedNode;
                if (typeRef.packageBinding == null) {
                    binding = typeRef.resolvedType;
                }
            } else if (this.selectedNode instanceof JavadocQualifiedTypeReference) {
                JavadocQualifiedTypeReference typeRef = (JavadocQualifiedTypeReference) this.selectedNode;
                if (typeRef.packageBinding == null) {
                    binding = typeRef.resolvedType;
                }
            } else {
                binding = this.selectedNode.resolvedType;
            }
            throw new SelectionNodeFound(binding);
        } else if (this.inheritDocSelected) {
            // no selection node when inheritDoc tag is selected
            // But we need to detect it to enable code select on inheritDoc
            ReferenceContext referenceContext = scope.referenceContext();
            if (referenceContext instanceof MethodDeclaration) {
                throw new SelectionNodeFound(((MethodDeclaration) referenceContext).binding);
            }
        }
    }

    /**
     * Resolve selected node if not null and throw exception to let clients know
     * that it has been found.
     */
    @Override
    public void resolve(ClassScope scope) {
        internalResolve(scope);
    }

    /**
     * Resolve selected node if not null and throw exception to let clients know
     * that it has been found.
     */
    @Override
    public void resolve(MethodScope scope) {
        internalResolve(scope);
    }
}

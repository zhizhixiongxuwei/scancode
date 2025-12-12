/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2024 IBM Corporation and others.
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
package org.eclipse.jdt.internal.core.dom;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.core.dom.util.DOMASTUtil;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion.
 * For various reasons the resulting string is not necessarily legal
 * Java code; and even if it is legal Java code, it is not necessarily the string
 * that corresponds to the given AST. Although useless for most purposes, it's
 * fine for generating debug print strings.
 * <p>
 * Example usage:
 * <pre>{@code
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * }</pre>
 * Call the <code>reset</code> method to clear the previous result before reusing an
 * existing instance.
 *
 * @since 2.0
 */
@SuppressWarnings("rawtypes")
public class NaiveASTFlattener extends ASTVisitor {

    /**
     * Internal synonym for {@link AST#JLS2}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    static final public int JLS2 = AST.JLS2;

    /**
     * Internal synonym for {@link AST#JLS3}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    static final public int JLS3 = AST.JLS3;

    /**
     * Internal synonym for {@link AST#JLS4}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.10
     */
    static final public int JLS4 = AST.JLS4;

    /**
     * Internal synonym for {@link AST#JLS8}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.14
     */
    static final public int JLS8 = AST.JLS8;

    /**
     * Internal synonym for {@link AST#JLS9}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.14
     */
    static final public int JLS9 = AST.JLS9;

    /**
     * Internal synonym for {@link AST#JLS14}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.22
     */
    private static final int JLS14 = AST.JLS14;

    /**
     * Internal synonym for {@link AST#JLS21}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     */
    private static final int JLS21 = AST.JLS21;

    /**
     * Internal synonym for {@link AST#JLS23}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     */
    private static final int JLS23 = AST.JLS23;

    /**
     * The string buffer into which the serialized representation of the AST is
     * written.
     */
    protected StringBuilder buffer;

    private int indent = 0;

    /**
     * Creates a new AST printer.
     */
    public NaiveASTFlattener() {
        this.buffer = new StringBuilder();
    }

    /**
     * Internal synonym for {@link ClassInstanceCreation#getName()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    private Name getName(ClassInstanceCreation node) {
        return node.getName();
    }

    /**
     * Returns the string accumulated in the visit.
     *
     * @return the serialized
     */
    public String getResult() {
        return this.buffer.toString();
    }

    /**
     * Internal synonym for {@link MethodDeclaration#getReturnType()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    private static Type getReturnType(MethodDeclaration node) {
        return node.getReturnType();
    }

    /**
     * Internal synonym for {@link TypeDeclaration#getSuperclass()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    private static Name getSuperclass(TypeDeclaration node) {
        return node.getSuperclass();
    }

    /**
     * Internal synonym for {@link TypeDeclarationStatement#getTypeDeclaration()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    private static TypeDeclaration getTypeDeclaration(TypeDeclarationStatement node) {
        return node.getTypeDeclaration();
    }

    /**
     * Internal synonym for {@link MethodDeclaration#thrownExceptions()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.10
     */
    private static List thrownExceptions(MethodDeclaration node) {
        return node.thrownExceptions();
    }

    void printIndent() {
        for (int i = 0; i < this.indent; i++) //$NON-NLS-1$
        this.buffer.append("  ");
    }

    /**
     * Appends the text representation of the given modifier flags, followed by a single space.
     * Used for JLS2 modifiers.
     *
     * @param modifiers the modifier flags
     */
    void printModifiers(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("public ");
        }
        if (Modifier.isProtected(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("protected ");
        }
        if (Modifier.isPrivate(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("private ");
        }
        if (Modifier.isStatic(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("static ");
        }
        if (Modifier.isAbstract(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("abstract ");
        }
        if (Modifier.isFinal(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("final ");
        }
        if (Modifier.isSynchronized(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("synchronized ");
        }
        if (Modifier.isVolatile(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("volatile ");
        }
        if (Modifier.isNative(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("native ");
        }
        if (Modifier.isStrictfp(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("strictfp ");
        }
        if (Modifier.isTransient(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("transient ");
        }
        if (Modifier.isSealed(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("sealed ");
        }
        if (Modifier.isNonSealed(modifiers)) {
            //$NON-NLS-1$
            this.buffer.append("non-sealed ");
        }
    }

    /**
     * Appends the text representation of the given modifier flags, followed by a single space.
     * Used for 3.0 modifiers and annotations.
     *
     * @param ext the list of modifier and annotation nodes
     * (element type: <code>IExtendedModifiers</code>)
     */
    void printModifiers(List ext) {
        for (Object element : ext) {
            ASTNode p = (ASTNode) element;
            p.accept(this);
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
    }

    /**
     * reference node helper function that is common to all
     * the difference reference nodes.
     *
     * @param typeArguments list of type arguments
     */
    private void visitReferenceTypeArguments(List typeArguments) {
        //$NON-NLS-1$
        this.buffer.append("::");
        if (!typeArguments.isEmpty()) {
            this.buffer.append('<');
            for (Iterator it = typeArguments.iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    this.buffer.append(',');
                }
            }
            this.buffer.append('>');
        }
    }

    private void visitTypeAnnotations(AnnotatableType node) {
        if (node.getAST().apiLevel() >= JLS8) {
            visitAnnotationsList(node.annotations());
        }
    }

    private void visitAnnotationsList(List annotations) {
        for (Object a : annotations) {
            Annotation annotation = (Annotation) a;
            annotation.accept(this);
            this.buffer.append(' ');
        }
    }

    /**
     * Resets this printer so that it can be used again.
     */
    public void reset() {
        this.buffer.setLength(0);
    }

    /**
     * Internal synonym for {@link TypeDeclaration#superInterfaces()}. Use to alleviate
     * deprecation warnings.
     * @deprecated
     * @since 3.4
     */
    private List superInterfaces(TypeDeclaration node) {
        return node.superInterfaces();
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        //$NON-NLS-1$
        this.buffer.append("@interface ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" {");
        for (Object element : node.bodyDeclarations()) {
            BodyDeclaration d = (BodyDeclaration) element;
            d.accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("()");
        if (node.getDefault() != null) {
            //$NON-NLS-1$
            this.buffer.append(" default ");
            node.getDefault().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Object element : node.bodyDeclarations()) {
            BodyDeclaration b = (BodyDeclaration) element;
            b.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        node.getArray().accept(this);
        //$NON-NLS-1$
        this.buffer.append("[");
        node.getIndex().accept(this);
        //$NON-NLS-1$
        this.buffer.append("]");
        return false;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        //$NON-NLS-1$
        this.buffer.append("new ");
        ArrayType at = node.getType();
        int dims = at.getDimensions();
        Type elementType = at.getElementType();
        elementType.accept(this);
        for (Object element : node.dimensions()) {
            //$NON-NLS-1$
            this.buffer.append("[");
            Expression e = (Expression) element;
            e.accept(this);
            //$NON-NLS-1$
            this.buffer.append("]");
            dims--;
        }
        // add empty "[]" for each extra array dimension
        for (int i = 0; i < dims; i++) {
            //$NON-NLS-1$
            this.buffer.append("[]");
        }
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        //$NON-NLS-1$
        this.buffer.append("{");
        for (Iterator it = node.expressions().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("}");
        return false;
    }

    @Override
    public boolean visit(ArrayType node) {
        if (node.getAST().apiLevel() < JLS8) {
            visitComponentType(node);
            //$NON-NLS-1$
            this.buffer.append("[]");
        } else {
            node.getElementType().accept(this);
            List dimensions = node.dimensions();
            int size = dimensions.size();
            for (int i = 0; i < size; i++) {
                Dimension aDimension = (Dimension) dimensions.get(i);
                aDimension.accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(AssertStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("assert ");
        node.getExpression().accept(this);
        if (node.getMessage() != null) {
            //$NON-NLS-1$
            this.buffer.append(" : ");
            node.getMessage().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(Assignment node) {
        node.getLeftHandSide().accept(this);
        this.buffer.append(node.getOperator().toString());
        node.getRightHandSide().accept(this);
        return false;
    }

    @Override
    public boolean visit(Block node) {
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Object element : node.statements()) {
            Statement s = (Statement) element;
            s.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(BlockComment node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("/* */");
        return false;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue() == true) {
            //$NON-NLS-1$
            this.buffer.append("true");
        } else {
            //$NON-NLS-1$
            this.buffer.append("false");
        }
        return false;
    }

    @Override
    public boolean visit(BreakStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("break");
        if (node.getLabel() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getLabel().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(CaseDefaultExpression node) {
        if (DOMASTUtil.isPatternSupported(node.getAST())) {
            //$NON-NLS-1$
            this.buffer.append("default");
        }
        return false;
    }

    @Override
    public boolean visit(CastExpression node) {
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        node.getExpression().accept(this);
        return false;
    }

    @Override
    public boolean visit(CatchClause node) {
        //$NON-NLS-1$
        this.buffer.append("catch (");
        node.getException().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(CharacterLiteral node) {
        this.buffer.append(node.getEscapedValue());
        return false;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("new ");
        if (node.getAST().apiLevel() == JLS2) {
            getName(node).accept(this);
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
            node.getType().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(CompilationUnit node) {
        if (node.getAST().apiLevel() >= JLS9) {
            if (node.getModule() != null) {
                node.getModule().accept(this);
            }
        }
        if (node.getPackage() != null) {
            node.getPackage().accept(this);
        }
        for (Object element : node.imports()) {
            ImportDeclaration d = (ImportDeclaration) element;
            d.accept(this);
        }
        for (Object element : node.types()) {
            AbstractTypeDeclaration d = (AbstractTypeDeclaration) element;
            d.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ? ");
        node.getThenExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" : ");
        node.getElseExpression().accept(this);
        return false;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        printIndent();
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("this(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("continue");
        if (node.getLabel() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getLabel().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(CreationReference node) {
        node.getType().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        //$NON-NLS-1$
        this.buffer.append("new");
        return false;
    }

    @Override
    public boolean visit(Dimension node) {
        List annotations = node.annotations();
        if (annotations.size() > 0)
            this.buffer.append(' ');
        visitAnnotationsList(annotations);
        //$NON-NLS-1$
        this.buffer.append("[]");
        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("do ");
        node.getBody().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" while (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("for (");
        node.getParameter().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" : ");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        node.getName().accept(this);
        if (!node.arguments().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("(");
            for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
                Expression e = (Expression) it.next();
                e.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(",");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(")");
        }
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        //$NON-NLS-1$
        this.buffer.append("enum ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        if (!node.superInterfaceTypes().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("implements ");
            for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(", ");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
        //$NON-NLS-1$
        this.buffer.append("{");
        for (Iterator it = node.enumConstants().iterator(); it.hasNext(); ) {
            EnumConstantDeclaration d = (EnumConstantDeclaration) it.next();
            d.accept(this);
            // enum constant declarations do not include punctuation
            if (it.hasNext()) {
                // enum constant declarations are separated by commas
                //$NON-NLS-1$
                this.buffer.append(", ");
            }
        }
        if (!node.bodyDeclarations().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("; ");
            for (Object element : node.bodyDeclarations()) {
                BodyDeclaration d = (BodyDeclaration) element;
                d.accept(this);
                // other body declarations include trailing punctuation
            }
        }
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(ExportsDirective node) {
        //$NON-NLS-1$
        return visit(node, "exports");
    }

    @Override
    public boolean visit(ExpressionMethodReference node) {
        node.getExpression().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        printIndent();
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(FieldAccess node) {
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(", ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(ForStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("for (");
        for (Iterator it = node.initializers().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            //$NON-NLS-1$
            if (it.hasNext())
                this.buffer.append(", ");
        }
        //$NON-NLS-1$
        this.buffer.append("; ");
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("; ");
        for (Iterator it = node.updaters().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            //$NON-NLS-1$
            if (it.hasNext())
                this.buffer.append(", ");
        }
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(GuardedPattern node) {
        if (DOMASTUtil.isPatternSupported(node.getAST())) {
            node.getPattern().accept(this);
            //$NON-NLS-1$
            this.buffer.append(" when ");
            node.getExpression().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(IfStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("if (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getThenStatement().accept(this);
        if (node.getElseStatement() != null) {
            //$NON-NLS-1$
            this.buffer.append(" else ");
            node.getElseStatement().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("import ");
        if (node.getAST().apiLevel() >= JLS23) {
            if (node.modifiers().size() == 1) {
                this.buffer.append(((Modifier) node.modifiers().get(0)).getKeyword().toString()).append(' ');
            }
        } else if (node.getAST().apiLevel() >= JLS3) {
            if (node.isStatic()) {
                //$NON-NLS-1$
                this.buffer.append("static ");
            }
        }
        node.getName().accept(this);
        if (node.isOnDemand()) {
            //$NON-NLS-1$
            this.buffer.append(".*");
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(InfixExpression node) {
        node.getLeftOperand().accept(this);
        // for cases like x= i - -1; or x= i++ + ++i;
        this.buffer.append(' ');
        this.buffer.append(node.getOperator().toString());
        this.buffer.append(' ');
        node.getRightOperand().accept(this);
        final List extendedOperands = node.extendedOperands();
        if (extendedOperands.size() != 0) {
            this.buffer.append(' ');
            for (Object extendedOperand : extendedOperands) {
                this.buffer.append(node.getOperator().toString()).append(' ');
                Expression e = (Expression) extendedOperand;
                e.accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(Initializer node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        node.getLeftOperand().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" instanceof ");
        node.getRightOperand().accept(this);
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean visit(PatternInstanceofExpression node) {
        node.getLeftOperand().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" instanceof ");
        if (node.getAST().apiLevel() >= JLS21) {
            node.getPattern().accept(this);
        } else {
            node.getRightOperand().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(IntersectionType node) {
        for (Iterator it = node.types().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(" & ");
            }
        }
        return false;
    }

    @Override
    public boolean visit(Javadoc node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("/** ");
        for (Object element : node.tags()) {
            ASTNode e = (ASTNode) element;
            e.accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("\n */\n");
        return false;
    }

    @Override
    public boolean visit(JavaDocRegion node) {
        //ToDO
        return false;
    }

    @Override
    public boolean visit(JavaDocTextElement node) {
        this.buffer.append(node.getText());
        return false;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        printIndent();
        node.getLabel().accept(this);
        //$NON-NLS-1$
        this.buffer.append(": ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(LambdaExpression node) {
        boolean hasParentheses = node.hasParentheses();
        if (hasParentheses)
            this.buffer.append('(');
        for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
            VariableDeclaration v = (VariableDeclaration) it.next();
            v.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        if (hasParentheses)
            this.buffer.append(')');
        //$NON-NLS-1$
        this.buffer.append(" -> ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(LineComment node) {
        //$NON-NLS-1$
        this.buffer.append("//\n");
        return false;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MemberRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("#");
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MemberValuePair node) {
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("=");
        node.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
            if (!node.typeParameters().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = (TypeParameter) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        if (!node.isConstructor()) {
            if (node.getAST().apiLevel() == JLS2) {
                getReturnType(node).accept(this);
            } else {
                if (node.getReturnType2() != null) {
                    node.getReturnType2().accept(this);
                } else {
                    // methods really ought to have a return type
                    //$NON-NLS-1$
                    this.buffer.append("void");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
        node.getName().accept(this);
        if (!(DOMASTUtil.isRecordDeclarationSupported(node.getAST()) && node.isCompactConstructor())) {
            //$NON-NLS-1$
            this.buffer.append("(");
            if (node.getAST().apiLevel() >= JLS8) {
                Type receiverType = node.getReceiverType();
                if (receiverType != null) {
                    receiverType.accept(this);
                    this.buffer.append(' ');
                    SimpleName qualifier = node.getReceiverQualifier();
                    if (qualifier != null) {
                        qualifier.accept(this);
                        this.buffer.append('.');
                    }
                    //$NON-NLS-1$
                    this.buffer.append("this");
                    if (node.parameters().size() > 0) {
                        this.buffer.append(',');
                    }
                }
            }
            for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
                SingleVariableDeclaration v = (SingleVariableDeclaration) it.next();
                v.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(",");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(")");
        }
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append("[]");
            }
        }
        if (node.getAST().apiLevel() < JLS8) {
            if (!thrownExceptions(node).isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(" throws ");
                for (Iterator it = thrownExceptions(node).iterator(); it.hasNext(); ) {
                    Name n = (Name) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        } else {
            if (!node.thrownExceptionTypes().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append(" throws ");
                for (Iterator it = node.thrownExceptionTypes().iterator(); it.hasNext(); ) {
                    Type n = (Type) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        }
        if (node.getBody() == null) {
            //$NON-NLS-1$
            this.buffer.append(";\n");
        } else {
            node.getBody().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    @Override
    public boolean visit(MethodRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append("#");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.parameters().iterator(); it.hasNext(); ) {
            MethodRefParameter e = (MethodRefParameter) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    @Override
    public boolean visit(MethodRefParameter node) {
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.isVarargs()) {
                //$NON-NLS-1$
                this.buffer.append("...");
            }
        }
        if (node.getName() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getName().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(Modifier node) {
        this.buffer.append(node.getKeyword().toString());
        return false;
    }

    @Override
    public boolean visit(ModuleDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printModifiers(node.annotations());
        if (node.isOpen())
            //$NON-NLS-1$
            this.buffer.append("open ");
        //$NON-NLS-1$
        this.buffer.append("module");
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" {\n");
        this.indent++;
        for (ModuleDirective stmt : (List<ModuleDirective>) node.moduleStatements()) {
            stmt.accept(this);
        }
        this.indent--;
        //$NON-NLS-1$
        this.buffer.append("}");
        return false;
    }

    @Override
    public /*
	 * @see ASTVisitor#visit(ModuleModifier)
	 * @since 3.14
	 */
    boolean visit(ModuleModifier node) {
        this.buffer.append(node.getKeyword().toString());
        return false;
    }

    private boolean visit(ModulePackageAccess node, String keyword) {
        this.buffer.append(keyword);
        if (node.getName() != null) {
            this.buffer.append(' ');
            node.getName().accept(this);
        }
        // Handle target modules as Names instead of Types
        if (!node.modules().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append(" to ");
            for (Iterator it = node.modules().iterator(); it.hasNext(); ) {
                Name moduleName = (Name) it.next();
                moduleName.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(",");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(";\n");
        }
        return false;
    }

    @Override
    public boolean visit(NameQualifiedType node) {
        node.getQualifier().accept(this);
        this.buffer.append('.');
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.values().iterator(); it.hasNext(); ) {
            MemberValuePair p = (MemberValuePair) it.next();
            p.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    @Override
    public boolean visit(NullLiteral node) {
        //$NON-NLS-1$
        this.buffer.append("null");
        return false;
    }

    @Override
    public boolean visit(NullPattern node) {
        if (DOMASTUtil.isPatternSupported(node.getAST())) {
            //$NON-NLS-1$
            this.buffer.append("null");
        }
        return false;
    }

    @Override
    public boolean visit(NumberLiteral node) {
        this.buffer.append(node.getToken());
        return false;
    }

    @Override
    public boolean visit(OpensDirective node) {
        //$NON-NLS-1$
        return visit(node, "opens");
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.getJavadoc() != null) {
                node.getJavadoc().accept(this);
            }
            for (Object element : node.annotations()) {
                Annotation p = (Annotation) element;
                p.accept(this);
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        }
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("package ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(ParameterizedType node) {
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append("<");
        for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(">");
        return false;
    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        node.getOperand().accept(this);
        this.buffer.append(node.getOperator().toString());
        return false;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        this.buffer.append(node.getOperator().toString());
        node.getOperand().accept(this);
        return false;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        visitTypeAnnotations(node);
        this.buffer.append(node.getPrimitiveTypeCode().toString());
        return false;
    }

    @Override
    public boolean visit(ProvidesDirective node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("provides");
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        // Replace printTypes() with proper Name handling
        List implementations = node.implementations();
        if (!implementations.isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append(" with ");
            for (Iterator it = implementations.iterator(); it.hasNext(); ) {
                Name implName = (Name) it.next();
                implName.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(", ");
                }
            }
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(ModuleQualifiedName node) {
        node.getModuleQualifier().accept(this);
        //$NON-NLS-1$
        this.buffer.append("/");
        ASTNode cNode = node.getName();
        if (cNode != null) {
            cNode.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(QualifiedName node) {
        node.getQualifier().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(QualifiedType node) {
        node.getQualifier().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".");
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(RecordDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printIndent();
        printModifiers(node.modifiers());
        //$NON-NLS-1$
        this.buffer.append("record ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        if (!node.typeParameters().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("<");
            for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
                TypeParameter t = (TypeParameter) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(",");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(">");
        }
        //$NON-NLS-1$
        this.buffer.append(" ");
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.recordComponents().iterator(); it.hasNext(); ) {
            SingleVariableDeclaration v = (SingleVariableDeclaration) it.next();
            v.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        if (!node.superInterfaceTypes().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append(" implements ");
            for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(", ");
                }
            }
            //$NON-NLS-1$
            this.buffer.append(" ");
        }
        //$NON-NLS-1$
        this.buffer.append("{");
        if (!node.bodyDeclarations().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append("\n");
            for (Object element : node.bodyDeclarations()) {
                BodyDeclaration d = (BodyDeclaration) element;
                d.accept(this);
                // other body declarations include trailing punctuation
            }
        }
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(RecordPattern node) {
        if (DOMASTUtil.isPatternSupported(node.getAST())) {
            if (node.getPatternType() != null) {
                node.getPatternType().accept(this);
            }
            boolean addBraces = node.patterns().size() >= 1;
            if (addBraces) {
                //$NON-NLS-1$
                this.buffer.append("(");
            }
            int size = 1;
            for (Pattern pattern : node.patterns()) {
                visitPattern(pattern);
                if (addBraces && size < node.patterns().size()) {
                    //$NON-NLS-1$
                    this.buffer.append(", ");
                }
                size++;
            }
            if (addBraces) {
                //$NON-NLS-1$
                this.buffer.append(")");
            }
        }
        return false;
    }

    @Override
    public boolean visit(EitherOrMultiPattern node) {
        if (DOMASTUtil.isEitherOrMultiPatternSupported(node.getAST())) {
            int size = 1;
            for (Pattern pattern : node.patterns()) {
                visitPattern(pattern);
                if (size < node.patterns().size()) {
                    //$NON-NLS-1$
                    this.buffer.append(", ");
                }
                size++;
            }
        }
        return false;
    }

    private boolean visitPattern(Pattern node) {
        if (!DOMASTUtil.isPatternSupported(node.getAST())) {
            return false;
        }
        if (node instanceof RecordPattern) {
            return visit((RecordPattern) node);
        }
        if (node instanceof GuardedPattern) {
            return visit((GuardedPattern) node);
        }
        if (node instanceof TypePattern) {
            return visit((TypePattern) node);
        }
        if (node instanceof EitherOrMultiPattern) {
            return visit((EitherOrMultiPattern) node);
        }
        return false;
    }

    @Override
    public boolean visit(RequiresDirective node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("requires");
        //$NON-NLS-1$
        this.buffer.append(" ");
        printModifiers(node.modifiers());
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("return");
        if (node.getExpression() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(SimpleName node) {
        this.buffer.append(node.getIdentifier());
        return false;
    }

    @Override
    public boolean visit(SimpleType node) {
        visitTypeAnnotations(node);
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        //$NON-NLS-1$
        this.buffer.append("@");
        node.getTypeName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        node.getValue().accept(this);
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.isVarargs()) {
                if (node.getAST().apiLevel() >= JLS8) {
                    List annotations = node.varargsAnnotations();
                    if (annotations.size() > 0) {
                        this.buffer.append(' ');
                    }
                    visitAnnotationsList(annotations);
                }
                //$NON-NLS-1$
                this.buffer.append("...");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append("[]");
            }
        }
        if (node.getInitializer() != null) {
            //$NON-NLS-1$
            this.buffer.append("=");
            node.getInitializer().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(StringLiteral node) {
        this.buffer.append(node.getEscapedValue());
        return false;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        printIndent();
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("super(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(");\n");
        return false;
    }

    @Override
    public boolean visit(SuperFieldAccess node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("super.");
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("super.");
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeArguments().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append("(");
        for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            e.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(",");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(")");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(SuperMethodReference)
	 *
	 * @since 3.10
	 */
    @Override
    public boolean visit(SuperMethodReference node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            this.buffer.append('.');
        }
        //$NON-NLS-1$
        this.buffer.append("super");
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SwitchCase node) {
        if ((node.getAST().apiLevel() >= JLS14)) {
            if (node.isDefault() && !isCaseDefaultExpression(node)) {
                //$NON-NLS-1$
                this.buffer.append("default");
                //$NON-NLS-1$ //$NON-NLS-2$
                this.buffer.append(node.isSwitchLabeledRule() ? " ->" : ":");
            } else {
                //$NON-NLS-1$
                this.buffer.append("case ");
                for (Iterator it = node.expressions().iterator(); it.hasNext(); ) {
                    Expression t = (Expression) it.next();
                    t.accept(this);
                    this.buffer.append(//$NON-NLS-1$
                    it.hasNext() ? //$NON-NLS-1$
                    ", " : //$NON-NLS-1$ //$NON-NLS-2$
                    node.isSwitchLabeledRule() ? " ->" : ":");
                }
            }
        } else {
            if (node.isDefault() && !isCaseDefaultExpression(node)) {
                //$NON-NLS-1$
                this.buffer.append("default :\n");
            } else {
                //$NON-NLS-1$
                this.buffer.append("case ");
                getSwitchExpression(node).accept(this);
                //$NON-NLS-1$
                this.buffer.append(":\n");
            }
        }
        //decremented in visit(SwitchStatement)
        this.indent++;
        return false;
    }

    private boolean isCaseDefaultExpression(SwitchCase node) {
        if (node.expressions() != null && node.expressions().size() == 1 && node.expressions().get(0) instanceof CaseDefaultExpression) {
            return true;
        }
        return false;
    }

    /**
     * @deprecated
     */
    private Expression getSwitchExpression(SwitchCase node) {
        return node.getExpression();
    }

    private void visitSwitchNode(ASTNode node) {
        //$NON-NLS-1$
        this.buffer.append("switch (");
        if (node instanceof SwitchExpression) {
            ((SwitchExpression) node).getExpression().accept(this);
        } else if (node instanceof SwitchStatement) {
            ((SwitchStatement) node).getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(") ");
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        if (node instanceof SwitchExpression) {
            for (Object element : ((SwitchExpression) node).statements()) {
                Statement s = (Statement) element;
                s.accept(this);
                // incremented in visit(SwitchCase)
                this.indent--;
            }
        } else if (node instanceof SwitchStatement) {
            for (Object element : ((SwitchStatement) node).statements()) {
                Statement s = (Statement) element;
                s.accept(this);
                // incremented in visit(SwitchCase)
                this.indent--;
            }
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
    }

    @Override
    public boolean visit(SwitchExpression node) {
        visitSwitchNode(node);
        return false;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        visitSwitchNode(node);
        return false;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        //$NON-NLS-1$
        this.buffer.append("synchronized (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(TagElement node) {
        if (node.isNested()) {
            // nested tags are always enclosed in braces
            //$NON-NLS-1$
            this.buffer.append("{");
        } else {
            // top-level tags always begin on a new line
            //$NON-NLS-1$
            this.buffer.append("\n * ");
        }
        boolean previousRequiresWhiteSpace = false;
        if (node.getTagName() != null) {
            this.buffer.append(node.getTagName());
            previousRequiresWhiteSpace = true;
        }
        boolean previousRequiresNewLine = false;
        for (Object element : node.fragments()) {
            ASTNode e = (ASTNode) element;
            // Name, MemberRef, MethodRef, and nested TagElement do not include white space.
            // TextElements don't always include whitespace, see <https://bugs.eclipse.org/206518>.
            boolean currentIncludesWhiteSpace = false;
            if (e instanceof TextElement) {
                String text = ((TextElement) e).getText();
                if (text.length() > 0 && ScannerHelper.isWhitespace(text.charAt(0))) {
                    // workaround for https://bugs.eclipse.org/403735
                    currentIncludesWhiteSpace = true;
                }
            }
            if (previousRequiresNewLine && currentIncludesWhiteSpace) {
                //$NON-NLS-1$
                this.buffer.append("\n * ");
            }
            previousRequiresNewLine = currentIncludesWhiteSpace;
            // add space if required to separate
            if (previousRequiresWhiteSpace && !currentIncludesWhiteSpace) {
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
            e.accept(this);
            previousRequiresWhiteSpace = !currentIncludesWhiteSpace && !(e instanceof TagElement);
        }
        if (DOMASTUtil.isJavaDocCodeSnippetSupported(node.getAST().apiLevel())) {
            for (Object element : node.tagProperties()) {
                TagProperty tagProperty = (TagProperty) element;
                tagProperty.accept(this);
            }
        }
        if (node.isNested()) {
            //$NON-NLS-1$
            this.buffer.append("}");
        }
        return false;
    }

    @Override
    public boolean visit(TagProperty node) {
        //$NON-NLS-1$
        this.buffer.append("\n{");
        this.buffer.append(node.getName());
        //$NON-NLS-1$
        this.buffer.append(" = ");
        this.buffer.append(node.getStringValue());
        node.getNodeValue().accept(this);
        //$NON-NLS-1$
        this.buffer.append("}");
        return false;
    }

    @Override
    public boolean visit(TextBlock node) {
        this.buffer.append(node.getEscapedValue());
        return false;
    }

    @Override
    public boolean visit(TextElement node) {
        this.buffer.append(node.getText());
        return false;
    }

    @Override
    public boolean visit(ThisExpression node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            //$NON-NLS-1$
            this.buffer.append(".");
        }
        //$NON-NLS-1$
        this.buffer.append("this");
        return false;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("throw ");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(TryStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("try ");
        if (node.getAST().apiLevel() >= JLS4) {
            List resources = node.resources();
            if (!resources.isEmpty()) {
                this.buffer.append('(');
                for (Iterator it = resources.iterator(); it.hasNext(); ) {
                    Expression variable = (Expression) it.next();
                    variable.accept(this);
                    if (it.hasNext()) {
                        this.buffer.append(';');
                    }
                }
                this.buffer.append(')');
            }
        }
        node.getBody().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Object element : node.catchClauses()) {
            CatchClause cc = (CatchClause) element;
            cc.accept(this);
        }
        if (node.getFinally() != null) {
            //$NON-NLS-1$
            this.buffer.append(" finally ");
            node.getFinally().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(ImplicitTypeDeclaration node) {
        //javaDoc
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        //bodyDeclaration
        this.indent++;
        for (Object element : node.bodyDeclarations()) {
            BodyDeclaration d = (BodyDeclaration) element;
            d.accept(this);
        }
        this.indent--;
        printIndent();
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        //$NON-NLS-2$//$NON-NLS-1$
        this.buffer.append(node.isInterface() ? "interface " : "class ");
        node.getName().accept(this);
        if (node.getAST().apiLevel() >= JLS3) {
            if (!node.typeParameters().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("<");
                for (Iterator it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = (TypeParameter) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(",");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(">");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(" ");
        if (node.getAST().apiLevel() == JLS2) {
            if (getSuperclass(node) != null) {
                //$NON-NLS-1$
                this.buffer.append("extends ");
                getSuperclass(node).accept(this);
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
            if (!superInterfaces(node).isEmpty()) {
                //$NON-NLS-2$//$NON-NLS-1$
                this.buffer.append(node.isInterface() ? "extends " : "implements ");
                for (Iterator it = superInterfaces(node).iterator(); it.hasNext(); ) {
                    Name n = (Name) it.next();
                    n.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        }
        if (node.getAST().apiLevel() >= JLS3) {
            if (node.getSuperclassType() != null) {
                //$NON-NLS-1$
                this.buffer.append("extends ");
                node.getSuperclassType().accept(this);
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
            if (!node.superInterfaceTypes().isEmpty()) {
                //$NON-NLS-2$//$NON-NLS-1$
                this.buffer.append(node.isInterface() ? "extends " : "implements ");
                for (Iterator it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        }
        if (DOMASTUtil.isFeatureSupportedinAST(node.getAST(), Modifier.SEALED)) {
            if (!node.permittedTypes().isEmpty()) {
                //$NON-NLS-1$
                this.buffer.append("permits ");
                for (Iterator it = node.permittedTypes().iterator(); it.hasNext(); ) {
                    Type t = (Type) it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        //$NON-NLS-1$
                        this.buffer.append(", ");
                    }
                }
                //$NON-NLS-1$
                this.buffer.append(" ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append("{\n");
        this.indent++;
        for (Object element : node.bodyDeclarations()) {
            BodyDeclaration d = (BodyDeclaration) element;
            d.accept(this);
        }
        this.indent--;
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("}\n");
        return false;
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        if (node.getAST().apiLevel() == JLS2) {
            getTypeDeclaration(node).accept(this);
        }
        if (node.getAST().apiLevel() >= JLS3) {
            node.getDeclaration().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(TypeLiteral node) {
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(".class");
        return false;
    }

    /*
	 * @see ASTVisitor#visit(TypeMethodReference)
	 *
	 * @since 3.10
	 */
    @Override
    public boolean visit(TypeMethodReference node) {
        node.getType().accept(this);
        visitReferenceTypeArguments(node.typeArguments());
        node.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(TypeParameter node) {
        if (node.getAST().apiLevel() >= JLS8) {
            printModifiers(node.modifiers());
        }
        node.getName().accept(this);
        if (!node.typeBounds().isEmpty()) {
            //$NON-NLS-1$
            this.buffer.append(" extends ");
            for (Iterator it = node.typeBounds().iterator(); it.hasNext(); ) {
                Type t = (Type) it.next();
                t.accept(this);
                if (it.hasNext()) {
                    //$NON-NLS-1$
                    this.buffer.append(" & ");
                }
            }
        }
        return false;
    }

    @Override
    public boolean visit(TypePattern node) {
        if (DOMASTUtil.isPatternSupported(node.getAST())) {
            if (node.getAST().apiLevel() < AST.JLS22) {
                node.getPatternVariable().accept(this);
            } else {
                node.getPatternVariable2().accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(UnionType node) {
        for (Iterator it = node.types().iterator(); it.hasNext(); ) {
            Type t = (Type) it.next();
            t.accept(this);
            if (it.hasNext()) {
                this.buffer.append('|');
            }
        }
        return false;
    }

    @Override
    public boolean visit(UsesDirective node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("uses");
        //$NON-NLS-1$
        this.buffer.append(" ");
        node.getName().accept(this);
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(", ");
            }
        }
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        node.getName().accept(this);
        int size = node.getExtraDimensions();
        if (node.getAST().apiLevel() >= JLS8) {
            List dimensions = node.extraDimensions();
            for (int i = 0; i < size; i++) {
                visit((Dimension) dimensions.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                //$NON-NLS-1$
                this.buffer.append("[]");
            }
        }
        if (node.getInitializer() != null) {
            //$NON-NLS-1$
            this.buffer.append("=");
            node.getInitializer().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        printIndent();
        if (node.getAST().apiLevel() == JLS2) {
            printModifiers(node.getModifiers());
        }
        if (node.getAST().apiLevel() >= JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        //$NON-NLS-1$
        this.buffer.append(" ");
        for (Iterator it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = (VariableDeclarationFragment) it.next();
            f.accept(this);
            if (it.hasNext()) {
                //$NON-NLS-1$
                this.buffer.append(", ");
            }
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    @Override
    public boolean visit(WhileStatement node) {
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("while (");
        node.getExpression().accept(this);
        //$NON-NLS-1$
        this.buffer.append(") ");
        node.getBody().accept(this);
        return false;
    }

    @Override
    public boolean visit(WildcardType node) {
        visitTypeAnnotations(node);
        //$NON-NLS-1$
        this.buffer.append("?");
        Type bound = node.getBound();
        if (bound != null) {
            if (node.isUpperBound()) {
                //$NON-NLS-1$
                this.buffer.append(" extends ");
            } else {
                //$NON-NLS-1$
                this.buffer.append(" super ");
            }
            bound.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(YieldStatement node) {
        if ((node.getAST().apiLevel() >= JLS14) && node.isImplicit() && node.getExpression() == null) {
            return false;
        }
        printIndent();
        //$NON-NLS-1$
        this.buffer.append("yield");
        if (node.getExpression() != null) {
            //$NON-NLS-1$
            this.buffer.append(" ");
            node.getExpression().accept(this);
        }
        //$NON-NLS-1$
        this.buffer.append(";\n");
        return false;
    }

    /**
     * @deprecated
     */
    private void visitComponentType(ArrayType node) {
        node.getComponentType().accept(this);
    }
}

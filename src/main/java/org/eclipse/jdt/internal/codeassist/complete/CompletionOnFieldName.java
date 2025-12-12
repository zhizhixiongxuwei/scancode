/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2009 IBM Corporation and others.
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
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public class CompletionOnFieldName extends FieldDeclaration implements CompletionNode {

    //$NON-NLS-1$
    static final public char[] FAKENAMESUFFIX = " ".toCharArray();

    public char[] realName;

    public CompletionOnFieldName(char[] name, int sourceStart, int sourceEnd) {
        super(CharOperation.concat(name, FAKENAMESUFFIX), sourceStart, sourceEnd);
        this.realName = name;
    }

    @Override
    public StringBuilder printStatement(int tab, StringBuilder output) {
        //$NON-NLS-1$
        printIndent(tab, output).append("<CompleteOnFieldName:");
        if (this.type != null)
            this.type.print(0, output).append(' ');
        output.append(this.realName);
        if (this.initialization != null) {
            //$NON-NLS-1$
            output.append(" = ");
            this.initialization.printExpression(0, output);
        }
        //$NON-NLS-1$
        return output.append(">;");
    }

    @Override
    public void resolve(MethodScope initializationScope) {
        super.resolve(initializationScope);
        throw new CompletionNodeFound(this, initializationScope);
    }
}

/**
 * ****************************************************************************
 *  Copyright (c) 2020 Gayan Perera and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      Gayan Perera - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class CompletionOnRecordComponentName extends RecordComponent implements CompletionNode {

    public CompletionOnRecordComponentName(char[] name, long posNom, TypeReference tr, int modifiers) {
        super(CharOperation.concat(name, FAKENAMESUFFIX), posNom, tr, modifiers);
        this.realName = name;
    }

    //$NON-NLS-1$
    static final public char[] FAKENAMESUFFIX = " ".toCharArray();

    public char[] realName;

    @Override
    public StringBuilder printStatement(int tab, StringBuilder output) {
        //$NON-NLS-1$
        printIndent(tab, output).append("<CompletionOnRecordComponentName:");
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
    public void resolve(BlockScope scope) {
        super.resolve(scope);
        throw new CompletionNodeFound(this, scope);
    }
}

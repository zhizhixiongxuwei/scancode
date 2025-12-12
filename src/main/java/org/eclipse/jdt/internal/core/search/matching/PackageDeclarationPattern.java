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
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.jdt.internal.core.index.EntryResult;
import org.eclipse.jdt.internal.core.index.Index;

public class PackageDeclarationPattern extends JavaSearchPattern {

    public char[] pkgName;

    public PackageDeclarationPattern(char[] pkgName, int matchRule) {
        super(PKG_DECL_PATTERN, matchRule);
        this.pkgName = pkgName;
    }

    @Override
    public EntryResult[] queryIn(Index index) {
        // package declarations are not indexed
        return null;
    }

    @Override
    protected StringBuilder print(StringBuilder output) {
        //$NON-NLS-1$
        output.append("PackageDeclarationPattern: <");
        if (this.pkgName != null)
            output.append(this.pkgName);
        else
            //$NON-NLS-1$
            output.append("*");
        //$NON-NLS-1$
        output.append(">");
        return super.print(output);
    }
}

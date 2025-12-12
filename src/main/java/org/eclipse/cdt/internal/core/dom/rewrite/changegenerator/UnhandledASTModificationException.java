/**
 * ****************************************************************************
 *  Copyright (c) 2008, 2011 Institute for Software, HSR Hochschule fuer Technik
 *  Rapperswil, University of applied sciences and others
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      Institute for Software - initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.dom.rewrite.changegenerator;

import org.eclipse.cdt.internal.core.dom.rewrite.ASTModification;

public class UnhandledASTModificationException extends RuntimeException {

    final public ASTModification illegalModification;

    public UnhandledASTModificationException(ASTModification illegalModification) {
        this.illegalModification = illegalModification;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        //$NON-NLS-1$
        message.append("Tried to ").append(illegalModification.getKind().name()).append(//$NON-NLS-1$
        " on ").append(illegalModification.getTargetNode()).append(//$NON-NLS-1$
        " with ").append(illegalModification.getNewNode());
        return message.toString();
    }
}

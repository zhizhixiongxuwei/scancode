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
package org.eclipse.jdt.internal.core.builder;

/**
 * Exception thrown when the build should be aborted because a source file is missing/empty.
 */
public class MissingSourceFileException extends RuntimeException {

    public String missingSourceFile;

    // backward compatible
    static final public long serialVersionUID = -1416609004971115719L;

    public MissingSourceFileException(String missingSourceFile) {
        this.missingSourceFile = missingSourceFile;
    }
}

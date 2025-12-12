/**
 * ****************************************************************************
 *  Copyright (c) 2005, 2009 IBM Corporation and others.
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
package org.eclipse.jdt.internal.core;

import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.internal.core.util.Util;

public class ClasspathAttribute implements IClasspathAttribute {

    final public String name;

    final public String value;

    public ClasspathAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClasspathAttribute))
            return false;
        ClasspathAttribute other = (ClasspathAttribute) obj;
        return this.name.equals(other.name) && this.value.equals(other.value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Util.combineHashCodes(this.name.hashCode(), this.value.hashCode());
    }

    @Override
    public String toString() {
        //$NON-NLS-1$
        return this.name + "=" + this.value;
    }
}

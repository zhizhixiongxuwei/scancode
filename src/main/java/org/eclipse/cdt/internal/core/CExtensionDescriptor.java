/**
 * ****************************************************************************
 *  Copyright (c) 2007, 2011 Intel Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Intel Corporation - Initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core;

import org.eclipse.cdt.core.ICExtensionDescriptor;
import org.eclipse.core.runtime.IConfigurationElement;

public class CExtensionDescriptor implements ICExtensionDescriptor {

    public IConfigurationElement fElement;

    //$NON-NLS-1$
    static final public String ATTRIBUTE_ID = "id";

    //$NON-NLS-1$
    static final public String ATTRIBUTE_NAME = "name";

    public CExtensionDescriptor(IConfigurationElement el) {
        fElement = el;
    }

    @Override
    public String getId() {
        return fElement.getAttribute(ATTRIBUTE_ID);
    }

    @Override
    public String getName() {
        return fElement.getAttribute(ATTRIBUTE_NAME);
    }

    public IConfigurationElement getConfigurationElement() {
        return fElement;
    }
}

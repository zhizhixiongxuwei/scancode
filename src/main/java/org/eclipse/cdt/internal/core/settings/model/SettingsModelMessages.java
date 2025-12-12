/**
 * ****************************************************************************
 *  Copyright (c) 2007 Intel Corporation and others.
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
package org.eclipse.cdt.internal.core.settings.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SettingsModelMessages {

    //$NON-NLS-1$
    static final public String BUNDLE_NAME = "org.eclipse.cdt.internal.core.settings.model.SettingsModelMessages";

    static final public ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private SettingsModelMessages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}

/**
 * ****************************************************************************
 *  Copyright (c) 2000, 2016 QNX Software Systems and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      QNX Software Systems - Initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.utils.debug.stabs;

import org.eclipse.cdt.core.CCorePlugin;

/**
 * Format: string_field = name ':' symbol-descriptor type-information
 */
public class StringField {

    public String name;

    public char symbolDescriptor;

    public String typeInformation;

    public StringField(String s) {
        parseStringField(s.toCharArray());
    }

    public String getName() {
        return name;
    }

    public char getSymbolDescriptor() {
        return symbolDescriptor;
    }

    public String getTypeInformation() {
        return typeInformation;
    }

    /**
     * Format: string_field = name ':' symbol-descriptor type-information
     */
    void parseStringField(char[] array) {
        int index = 0;
        // Some String field may contain format like:
        // "foo::bar::baz:t5=*6" in that case the name is "foo::bar::baz"
        char prev = 0;
        for (; index < array.length; index++) {
            char c = array[index];
            if (c == ':' && prev != ':') {
                break;
            }
            prev = c;
        }
        if (index < array.length) {
            name = new String(array, 0, index);
        } else {
            name = new String(array);
        }
        /* FIXME: Sometimes the special C++ names start with '.'. */
        if (name.length() > 1 && name.charAt(0) == '$') {
            switch(name.charAt(1)) {
                case 't':
                    //$NON-NLS-1$
                    name = "this";
                    break;
                case 'v':
                    /* Was: name = "vptr"; */
                    break;
                case 'e':
                    //$NON-NLS-1$
                    name = "eh_throw";
                    break;
                case '_':
                    /* This was an anonymous type that was never fixed up. */
                    break;
                case 'X':
                    /* SunPRO (3.0 at least) static variable encoding. */
                    break;
                default:
                    //$NON-NLS-1$
                    name = CCorePlugin.getResourceString("Util.unknownName");
                    break;
            }
        }
        // get the symbol descriptor
        if (index < array.length) {
            index++;
            if (Character.isLetter(array[index])) {
                symbolDescriptor = array[index];
                index++;
            }
        }
        // get the type-information
        if (index < array.length) {
            typeInformation = new String(array, index, array.length - index);
        } else {
            //$NON-NLS-1$
            typeInformation = "";
        }
    }
}

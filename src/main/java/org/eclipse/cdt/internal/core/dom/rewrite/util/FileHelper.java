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
package org.eclipse.cdt.internal.core.dom.rewrite.util;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.resources.ResourceLookup;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.TextUtilities;

public class FileHelper {

    //$NON-NLS-1$
    static final public String DEFAULT_LINE_DELIMITTER = "\n";

    public static IFile getFileFromNode(IASTNode node) {
        IPath implPath = new Path(node.getContainingFilename());
        return ResourceLookup.selectFileForLocation(implPath, null);
    }

    public static String determineLineDelimiter(String text) {
        //$NON-NLS-1$
        String platformDefaultLineDelimiter = System.getProperty("line.separator", DEFAULT_LINE_DELIMITTER);
        String defaultLineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, platformDefaultLineDelimiter, null);
        if (text.isEmpty()) {
            return defaultLineDelimiter;
        }
        return TextUtilities.determineLineDelimiter(text, defaultLineDelimiter);
    }
}

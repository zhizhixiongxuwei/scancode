/**
 * ****************************************************************************
 *  Copyright (c) 2004, 2015 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  IBM - Initial API and implementation
 * *****************************************************************************
 */
package org.eclipse.cdt.utils.som.parser;

import java.io.EOFException;
import java.io.IOException;
import org.eclipse.cdt.core.AbstractCExtension;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.IBinaryParser;
import org.eclipse.cdt.utils.DefaultGnuToolFactory;
import org.eclipse.cdt.utils.IGnuToolFactory;
import org.eclipse.cdt.utils.som.AR;
import org.eclipse.cdt.utils.som.SOM;
import org.eclipse.core.runtime.IPath;

/**
 * HP-UX SOM binary parser
 *
 * @author vhirsl
 */
public class SOMParser extends AbstractCExtension implements IBinaryParser {

    public DefaultGnuToolFactory toolFactory;

    @Override
    public IBinaryFile getBinary(byte[] hints, IPath path) throws IOException {
        if (path == null) {
            //$NON-NLS-1$
            throw new IOException(CCorePlugin.getResourceString("Util.exception.nullPath"));
        }
        IBinaryFile binary = null;
        try {
            SOM.Attribute attribute = null;
            if (hints != null && hints.length > 0) {
                try {
                    attribute = SOM.getAttributes(hints);
                } catch (EOFException eof) {
                    // continue, the array was to small.
                }
            }
            //Take a second run at it if the data array failed.
            if (attribute == null) {
                attribute = SOM.getAttributes(path.toOSString());
            }
            if (attribute != null) {
                switch(attribute.getType()) {
                    case SOM.Attribute.SOM_TYPE_EXE:
                        binary = createBinaryExecutable(path);
                        break;
                    case SOM.Attribute.SOM_TYPE_SHLIB:
                        binary = createBinaryShared(path);
                        break;
                    case SOM.Attribute.SOM_TYPE_OBJ:
                        binary = createBinaryObject(path);
                        break;
                    case SOM.Attribute.SOM_TYPE_CORE:
                        binary = createBinaryCore(path);
                        break;
                }
            }
        } catch (IOException e) {
            binary = createBinaryArchive(path);
        }
        return binary;
    }

    @Override
    public IBinaryFile getBinary(IPath path) throws IOException {
        return getBinary(null, path);
    }

    @Override
    public String getFormat() {
        //$NON-NLS-1$
        return "SOM";
    }

    @Override
    public boolean isBinary(byte[] hints, IPath path) {
        return SOM.isSOMHeader(hints) || AR.isARHeader(hints);
    }

    @Override
    public int getHintBufferSize() {
        // size of file header
        return 512;
    }

    /**
     * @param path
     * @return
     */
    private IBinaryExecutable createBinaryExecutable(IPath path) {
        return new SOMBinaryExecutable(this, path);
    }

    /**
     * @param path
     * @return
     */
    private IBinaryShared createBinaryShared(IPath path) {
        return new SOMBinaryShared(this, path);
    }

    /**
     * @param path
     * @return
     */
    private IBinaryObject createBinaryObject(IPath path) {
        return new SOMBinaryObject(this, path, IBinaryFile.OBJECT);
    }

    /**
     * @param path
     * @return
     */
    private IBinaryObject createBinaryCore(IPath path) {
        return new SOMBinaryObject(this, path, IBinaryFile.CORE);
    }

    /**
     * @param path
     * @return
     * @throws IOException
     */
    private IBinaryArchive createBinaryArchive(IPath path) throws IOException {
        return new SOMBinaryArchive(this, path);
    }

    protected DefaultGnuToolFactory createGNUToolFactory() {
        return new DefaultGnuToolFactory(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.equals(IGnuToolFactory.class)) {
            if (toolFactory == null) {
                toolFactory = createGNUToolFactory();
            }
            return (T) toolFactory;
        }
        return super.getAdapter(adapter);
    }
}

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
 *      Markus Schorn (Wind River Systems)
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.model;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICModelStatus;
import org.eclipse.cdt.core.model.ICModelStatusConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @see ICModelStatus
 */
public class CModelStatus extends Status implements ICModelStatus, ICModelStatusConstants {

    /**
     * The elements related to the failure, or <code>null</code> if no
     * elements are involved.
     */
    public ICElement[] fElements;

    final static public ICElement[] EmptyElement = new ICElement[] {};

    /**
     * The path related to the failure, or <code>null</code> if no path is
     * involved.
     */
    public IPath fPath;

    /**
     * The <code>String</code> related to the failure, or <code>null</code>
     * if no <code>String</code> is involved.
     */
    public String fString;

    //$NON-NLS-1$
    final static public String EMPTY_STRING = "";

    /**
     * Empty children
     */
    protected final static IStatus[] fgEmptyChildren = {};

    protected IStatus[] fChildren = fgEmptyChildren;

    //$NON-NLS-1$;
    protected final static String DEFAULT_STRING = "CModelStatus";

    /**
     * Singleton OK object
     */
    //$NON-NLS-1$
    public static final ICModelStatus VERIFIED_OK = new CModelStatus(OK, OK, CoreModelMessages.getString("status.OK"));

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus() {
        // no code for an multi-status
        this(0);
    }

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus(int code) {
        this(code, CElement.NO_ELEMENTS);
    }

    /**
     * Constructs an C model status with the given corresponding elements.
     */
    public CModelStatus(int code, ICElement[] elements) {
        super(ERROR, CCorePlugin.PLUGIN_ID, code, DEFAULT_STRING, null);
        fElements = elements;
        fPath = Path.EMPTY;
    }

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus(int code, String string) {
        this(ERROR, code, string);
    }

    public CModelStatus(int severity, int code, String string) {
        super(severity, CCorePlugin.PLUGIN_ID, code, DEFAULT_STRING, null);
        fElements = CElement.NO_ELEMENTS;
        fPath = Path.EMPTY;
        fString = string;
    }

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus(int code, IPath path) {
        super(ERROR, CCorePlugin.PLUGIN_ID, code, DEFAULT_STRING, null);
        fElements = CElement.NO_ELEMENTS;
        fPath = path;
    }

    /**
     * Constructs an C model status with the given corresponding element.
     */
    public CModelStatus(int code, ICElement element) {
        this(code, new ICElement[] { element });
    }

    /**
     * Constructs an C model status with the given corresponding element and
     * string
     */
    public CModelStatus(int code, ICElement element, String string) {
        this(code, new ICElement[] { element });
        fString = string;
    }

    public CModelStatus(int code, ICElement element, IPath path) {
        this(code, new ICElement[] { element });
        fPath = path;
    }

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus(CoreException coreException) {
        this(CORE_EXCEPTION, coreException);
    }

    /**
     * Constructs an C model status with no corresponding elements.
     */
    public CModelStatus(int code, Throwable throwable) {
        super(ERROR, CCorePlugin.PLUGIN_ID, code, DEFAULT_STRING, throwable);
        fElements = CElement.NO_ELEMENTS;
        fPath = Path.EMPTY;
    }

    protected int getBits() {
        int severity = 1 << (getCode() % 100 / 33);
        int category = 1 << ((getCode() / 100) + 3);
        return severity | category;
    }

    /**
     * @see IStatus
     */
    @Override
    public IStatus[] getChildren() {
        return fChildren;
    }

    /**
     * @see ICModelStatus
     */
    @Override
    public ICElement[] getElements() {
        return fElements;
    }

    /**
     * Returns the message that is relevant to the code of this status.
     */
    @Override
    public String getMessage() {
        Throwable exception = getException();
        if (isMultiStatus()) {
            StringBuilder sb = new StringBuilder();
            IStatus[] children = getChildren();
            if (children != null && children.length > 0) {
                for (int i = 0; i < children.length; ++i) {
                    sb.append(children[i].getMessage()).append(',');
                }
            }
            return sb.toString();
        }
        if (exception == null) {
            switch(getCode()) {
                case CORE_EXCEPTION:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.coreException");
                case DEVICE_PATH:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.cannotUseDeviceOnPath", getPath().toString());
                case PARSER_EXCEPTION:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.ParserError");
                case ELEMENT_DOES_NOT_EXIST:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.elementDoesNotExist", getFirstElementName());
                case EVALUATION_ERROR:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.evaluationError", getString());
                case INDEX_OUT_OF_BOUNDS:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.indexOutOfBounds");
                case INVALID_CONTENTS:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidContents");
                case INVALID_DESTINATION:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidDestination", getFirstElementName());
                case INVALID_ELEMENT_TYPES:
                    //$NON-NLS-1$
                    StringBuilder buff = new StringBuilder(CoreModelMessages.getFormattedString("operation.notSupported"));
                    for (int i = 0; i < fElements.length; i++) {
                        if (i > 0) {
                            //$NON-NLS-1$
                            buff.append(", ");
                        }
                        buff.append((fElements[i]).toString());
                    }
                    return buff.toString();
                case INVALID_NAME:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidName", getString());
                case INVALID_PATH:
                    //$NON-NLS-1$
                    String path = getPath() == null ? "null" : getPath().toString();
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidPath", new Object[] { path, getString() });
                case INVALID_PATHENTRY:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidPathEntry", getString());
                case INVALID_PROJECT:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidProject", getString());
                case INVALID_RESOURCE:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidResource", getString());
                case INVALID_RESOURCE_TYPE:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidResourceType", getString());
                case INVALID_SIBLING:
                    if (fString != null) {
                        //$NON-NLS-1$
                        return CoreModelMessages.getFormattedString("status.invalidSibling", getString());
                    }
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.invalidSibling", getFirstElementName());
                case IO_EXCEPTION:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.IOException");
                case NAME_COLLISION:
                    StringBuilder sb = new StringBuilder();
                    if (fElements != null && fElements.length > 0) {
                        ICElement element = fElements[0];
                        sb.append(element.getElementName()).append(' ');
                    }
                    if (fString != null) {
                        return fString;
                    }
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.nameCollision", sb.toString());
                case NO_ELEMENTS_TO_PROCESS:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("operation.needElements");
                case NULL_NAME:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("operation.needName");
                case NULL_PATH:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("operation.needPath");
                case NULL_STRING:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("operation.needString");
                case PATH_OUTSIDE_PROJECT:
                    return //$NON-NLS-1$
                    CoreModelMessages.//$NON-NLS-1$
                    getFormattedString(//$NON-NLS-1$
                    "operation.pathOutsideProject", new String[] { getString(), getFirstElementName() });
                case READ_ONLY:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.readOnly", getFirstElementName());
                case RELATIVE_PATH:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("operation.needAbsolutePath", getPath().toString());
                case UPDATE_CONFLICT:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.updateConflict");
                case NO_LOCAL_CONTENTS:
                    //$NON-NLS-1$
                    return CoreModelMessages.getFormattedString("status.noLocalContents", getPath().toString());
            }
            return getString();
        }
        String message = exception.getMessage();
        if (message != null) {
            return message;
        }
        return exception.toString();
    }

    @Override
    public IPath getPath() {
        if (fPath == null) {
            return Path.EMPTY;
        }
        return fPath;
    }

    /**
     * @see IStatus
     */
    @Override
    public int getSeverity() {
        if (fChildren == fgEmptyChildren)
            return super.getSeverity();
        int severity = -1;
        for (IStatus element : fChildren) {
            int childrenSeverity = element.getSeverity();
            if (childrenSeverity > severity) {
                severity = childrenSeverity;
            }
        }
        return severity;
    }

    /**
     * @see ICModelStatus
     */
    @Override
    public String getString() {
        if (fString == null) {
            return EMPTY_STRING;
        }
        return fString;
    }

    public String getFirstElementName() {
        if (fElements != null && fElements.length > 0) {
            return fElements[0].getElementName();
        }
        return EMPTY_STRING;
    }

    /**
     * @see ICModelStatus
     */
    @Override
    public boolean doesNotExist() {
        return getCode() == ELEMENT_DOES_NOT_EXIST;
    }

    /**
     * @see IStatus
     */
    @Override
    public boolean isMultiStatus() {
        return fChildren != fgEmptyChildren;
    }

    /**
     * @see ICModelStatus
     */
    @Override
    public boolean isOK() {
        return getCode() == OK;
    }

    /**
     * @see IStatus#matches
     */
    @Override
    public boolean matches(int mask) {
        if (!isMultiStatus()) {
            return matches(this, mask);
        }
        for (IStatus element : fChildren) {
            if (matches((CModelStatus) element, mask))
                return true;
        }
        return false;
    }

    /**
     * Helper for matches(int).
     */
    protected boolean matches(CModelStatus status, int mask) {
        int severityMask = mask & 0x7;
        int categoryMask = mask & ~0x7;
        int bits = status.getBits();
        return ((severityMask == 0) || (bits & severityMask) != 0) && ((categoryMask == 0) || (bits & categoryMask) != 0);
    }

    /**
     * Creates and returns a new <code>ICModelStatus</code> that is a a
     * multi-status status.
     *
     * @see IStatus#isMultiStatus()
     */
    public static ICModelStatus newMultiStatus(ICModelStatus[] children) {
        CModelStatus jms = new CModelStatus();
        jms.fChildren = children;
        return jms;
    }

    /**
     * Creates and returns a new <code>ICModelStatus</code> that is a a
     * multi-status status.
     */
    public static ICModelStatus newMultiStatus(int code, ICModelStatus[] children) {
        CModelStatus jms = new CModelStatus(code);
        jms.fChildren = children;
        return jms;
    }

    /**
     * Returns a printable representation of this exception for debugging
     * purposes.
     */
    @Override
    public String toString() {
        if (this == VERIFIED_OK) {
            //$NON-NLS-1$
            return "CModelStatus[OK]";
        }
        StringBuilder buffer = new StringBuilder();
        //$NON-NLS-1$
        buffer.append("C Model Status [");
        buffer.append(getMessage());
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }
}

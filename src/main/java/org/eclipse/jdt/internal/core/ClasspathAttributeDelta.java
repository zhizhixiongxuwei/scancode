/**
 * ****************************************************************************
 *  Copyright (c) 2022 Simeon Andreev and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * *****************************************************************************
 */
package org.eclipse.jdt.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathAttributeDelta;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * @see IClasspathAttributeDelta
 */
public class ClasspathAttributeDelta implements IClasspathAttributeDelta {

    //$NON-NLS-1$
    static final public String NO_VALUE = "";

    final public int kind;

    final public String attributeName;

    final public String attributeValue;

    private ClasspathAttributeDelta(int kind, String attributeName, String attributeValue) {
        //$NON-NLS-1$
        Assert.isTrue(kind == ADDED || kind == REMOVED || kind == CHANGED, "Unexpected delta kind: " + kind);
        this.kind = kind;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    public int getKind() {
        return this.kind;
    }

    @Override
    public String getAttributeName() {
        return this.attributeName;
    }

    @Override
    public String getAttributeValue() {
        return this.attributeValue;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        //$NON-NLS-1$
        buffer.append("attribute name=");
        buffer.append(getAttributeName());
        int deltaKind = getKind();
        if (deltaKind != REMOVED) {
            //$NON-NLS-1$
            buffer.append(", value=");
            buffer.append(getAttributeValue());
        }
        //$NON-NLS-1$
        buffer.append("[");
        switch(deltaKind) {
            case ADDED:
                buffer.append('+');
                break;
            case REMOVED:
                buffer.append('-');
                break;
            case CHANGED:
                buffer.append('*');
                break;
            default:
                buffer.append('?');
                break;
        }
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }

    static List<IClasspathAttributeDelta> getAttributeDeltas(IClasspathEntry oldClasspathEntry, IClasspathEntry newClasspathEntry) {
        Map<String, String> oldAttributes = getAttributes(oldClasspathEntry);
        Map<String, String> newAttributes = getAttributes(newClasspathEntry);
        if (oldAttributes.isEmpty() && newAttributes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> addedAttributes = new LinkedHashSet<>();
        Set<String> removedAttributes = new LinkedHashSet<>();
        Set<String> changedAttributes = new LinkedHashSet<>();
        for (Entry<String, String> newAttribute : newAttributes.entrySet()) {
            String name = newAttribute.getKey();
            if (!oldAttributes.containsKey(name)) {
                addedAttributes.add(name);
            }
        }
        for (Entry<String, String> oldAttribute : oldAttributes.entrySet()) {
            String name = oldAttribute.getKey();
            String oldValue = oldAttribute.getValue();
            String newValue = newAttributes.get(name);
            if (newValue != null) {
                if (!Objects.equals(oldValue, newValue)) {
                    changedAttributes.add(name);
                }
            } else {
                removedAttributes.add(name);
            }
        }
        List<IClasspathAttributeDelta> attributeDeltas = new ArrayList<>();
        for (String name : addedAttributes) {
            String value = newAttributes.get(name);
            IClasspathAttributeDelta attributeDelta = new ClasspathAttributeDelta(IClasspathAttributeDelta.ADDED, name, value);
            attributeDeltas.add(attributeDelta);
        }
        for (String name : removedAttributes) {
            String noValue = NO_VALUE;
            IClasspathAttributeDelta attributeDelta = new ClasspathAttributeDelta(IClasspathAttributeDelta.REMOVED, name, noValue);
            attributeDeltas.add(attributeDelta);
        }
        for (String name : changedAttributes) {
            String value = newAttributes.get(name);
            IClasspathAttributeDelta attributeDelta = new ClasspathAttributeDelta(IClasspathAttributeDelta.CHANGED, name, value);
            attributeDeltas.add(attributeDelta);
        }
        return attributeDeltas;
    }

    private static Map<String, String> getAttributes(IClasspathEntry classpathEntry) {
        IClasspathAttribute[] extraAttributes = classpathEntry.getExtraAttributes();
        Map<String, String> attributes = new LinkedHashMap<>();
        for (IClasspathAttribute attribute : extraAttributes) {
            String name = attribute.getName();
            String value = attribute.getValue();
            if (value == null) {
                value = NO_VALUE;
            }
            attributes.put(name, value);
        }
        return attributes;
    }
}

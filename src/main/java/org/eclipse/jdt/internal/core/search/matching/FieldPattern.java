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
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.util.Util;

public class FieldPattern extends VariablePattern {

    // declaring type
    public char[] declaringQualification;

    public char[] declaringSimpleName;

    // type
    public char[] typeQualification;

    public char[] typeSimpleName;

    static public char[][] REF_CATEGORIES = { REF };

    protected static char[][] REF_AND_DECL_CATEGORIES = { REF, FIELD_DECL };

    protected static char[][] DECL_CATEGORIES = { FIELD_DECL };

    public static char[] createIndexKey(char[] fieldName) {
        return fieldName;
    }

    public FieldPattern(char[] name, char[] declaringQualification, char[] declaringSimpleName, char[] typeQualification, char[] typeSimpleName, int limitTo, int matchRule) {
        super(FIELD_PATTERN, name, limitTo, matchRule);
        this.declaringQualification = this.isCaseSensitive ? declaringQualification : CharOperation.toLowerCase(declaringQualification);
        this.declaringSimpleName = this.isCaseSensitive ? declaringSimpleName : CharOperation.toLowerCase(declaringSimpleName);
        this.typeQualification = this.isCaseSensitive ? typeQualification : CharOperation.toLowerCase(typeQualification);
        this.typeSimpleName = (this.isCaseSensitive || this.isCamelCase) ? typeSimpleName : CharOperation.toLowerCase(typeSimpleName);
        this.mustResolve = mustResolve();
    }

    /*
 * Instantiate a field pattern with additional information for generic search
 */
    public FieldPattern(char[] name, char[] declaringQualification, char[] declaringSimpleName, char[] typeQualification, char[] typeSimpleName, String typeSignature, int limitTo, int matchRule) {
        this(name, declaringQualification, declaringSimpleName, typeQualification, typeSimpleName, limitTo, matchRule);
        // store type signatures and arguments
        if (typeSignature != null) {
            this.typeSignatures = Util.splitTypeLevelsSignature(typeSignature);
            setTypeArguments(Util.getAllTypeArguments(this.typeSignatures));
        }
    }

    @Override
    public void decodeIndexKey(char[] key) {
        this.name = key;
    }

    @Override
    public SearchPattern getBlankPattern() {
        return new FieldPattern(null, null, null, null, null, 0, R_EXACT_MATCH | R_CASE_SENSITIVE);
    }

    @Override
    public char[] getIndexKey() {
        return this.name;
    }

    @Override
    public char[][] getIndexCategories() {
        if (this.findReferences || this.fineGrain != 0)
            return this.findDeclarations || this.writeAccess ? REF_AND_DECL_CATEGORIES : REF_CATEGORIES;
        if (this.findDeclarations)
            return DECL_CATEGORIES;
        return CharOperation.NO_CHAR_CHAR;
    }

    @Override
    public boolean matchesDecodedKey(SearchPattern decodedPattern) {
        // index key is not encoded so query results all match
        return true;
    }

    @Override
    protected boolean mustResolve() {
        if (this.declaringSimpleName != null || this.declaringQualification != null)
            return true;
        if (this.typeSimpleName != null || this.typeQualification != null)
            return true;
        return super.mustResolve();
    }

    @Override
    protected StringBuilder print(StringBuilder output) {
        if (this.findDeclarations) {
            output.append(this.findReferences ? //$NON-NLS-1$
            "FieldCombinedPattern: " : //$NON-NLS-1$
            "FieldDeclarationPattern: ");
        } else {
            //$NON-NLS-1$
            output.append("FieldReferencePattern: ");
        }
        if (this.declaringQualification != null)
            output.append(this.declaringQualification).append('.');
        if (this.declaringSimpleName != null)
            output.append(this.declaringSimpleName).append('.');
        else //$NON-NLS-1$
        if (this.declaringQualification != null)
            output.append("*.");
        if (this.name == null) {
            //$NON-NLS-1$
            output.append("*");
        } else {
            output.append(this.name);
        }
        if (this.typeQualification != null)
            //$NON-NLS-1$
            output.append(" --> ").append(this.typeQualification).append('.');
        else //$NON-NLS-1$
        if (this.typeSimpleName != null)
            output.append(" --> ");
        if (this.typeSimpleName != null)
            output.append(this.typeSimpleName);
        else //$NON-NLS-1$
        if (this.typeQualification != null)
            output.append("*");
        return super.print(output);
    }
}

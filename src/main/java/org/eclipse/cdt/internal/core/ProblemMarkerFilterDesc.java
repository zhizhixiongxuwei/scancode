/**
 * ****************************************************************************
 *  Copyright (c) 2014 BlackBerry Limited and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core;

import java.util.Arrays;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.IProblemMarkerFilter;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * The purpose of ProblemMarkerFilterDesc is to manage information about
 * one instance ProblemMarkerFilter extension point.
 */
public class ProblemMarkerFilterDesc {

    /**
     * XML attribute for name of class that implements this extension point
     */
    //$NON-NLS-1$
    static final public String ATTR_CLASS = "class";

    /**
     * Variable name for projectNatures for enablement expression
     */
    //$NON-NLS-1$
    static final public String VAR_PROJECTNATURES = "projectNatures";

    /**
     * Configuration element for this extension point
     */
    final public IConfigurationElement element;

    /**
     * Expression that allows conditionally enable/disable extension point
     */
    final public Expression enablementExpression;

    /**
     * Status of this extension point.
     * False = disabled because of expression error
     * True = enabled because of missing enablementExpression
     * null = evaluate the expression for particular project
     */
    public Boolean fStatus = null;

    /**
     * Unique id of this extension point
     */
    private String id;

    /**
     */
    private IProblemMarkerFilter filter;

    /**
     * Filter that accept any marker.
     */
    private static final IProblemMarkerFilter NULL_FILTER = new IProblemMarkerFilter() {

        @Override
        public boolean acceptMarker(ProblemMarkerInfo markerInfo) {
            return true;
        }
    };

    /**
     * Constructor
     *
     * @param element configuration element with optional enablementExpression
     */
    ProblemMarkerFilterDesc(IConfigurationElement element) {
        this.element = element;
        Expression expr = null;
        IConfigurationElement[] children = element.getChildren(ExpressionTagNames.ENABLEMENT);
        switch(children.length) {
            case 0:
                fStatus = Boolean.TRUE;
                break;
            case 1:
                try {
                    ExpressionConverter parser = ExpressionConverter.getDefault();
                    expr = parser.perform(children[0]);
                } catch (CoreException e) {
                    //$NON-NLS-1$
                    CCorePlugin.log("Error in enablement expression of " + id, e);
                    fStatus = Boolean.FALSE;
                }
                break;
            default:
                //$NON-NLS-1$
                CCorePlugin.log("Too many enablement expressions for " + id);
                fStatus = Boolean.FALSE;
                break;
        }
        enablementExpression = expr;
    }

    /**
     * Evaluate enablement expression
     *
     * @param project project for which we had to evaluate the expression
     * @return value of enablement expression
     */
    public boolean matches(IProject project) {
        // If the enablement expression is missing or structurally invalid, then return immediately
        if (fStatus != null)
            return fStatus.booleanValue();
        if (enablementExpression != null)
            try {
                EvaluationContext evalContext = new EvaluationContext(null, project);
                String[] natures = project.getDescription().getNatureIds();
                evalContext.addVariable(VAR_PROJECTNATURES, Arrays.asList(natures));
                return enablementExpression.evaluate(evalContext) == EvaluationResult.TRUE;
            } catch (CoreException e) {
                //$NON-NLS-1$
                CCorePlugin.log("Error while evaluating enablement expression for " + id, e);
            }
        return false;
    }

    /**
     * Return filter interface
     * @return Filter interface or NULL_FILER if filter could not be created
     */
    IProblemMarkerFilter getFilter() {
        if (filter == null)
            synchronized (this) {
                if (filter == null) {
                    try {
                        filter = (IProblemMarkerFilter) element.createExecutableExtension(ATTR_CLASS);
                    } catch (CoreException e) {
                        String id = element.getDeclaringExtension().getNamespaceIdentifier() + '.' + element.getDeclaringExtension().getSimpleIdentifier();
                        //$NON-NLS-1$
                        CCorePlugin.log("Error in class attribute of " + id, e);
                        // mark the filter with an empty implementation to prevent future load attempts
                        filter = NULL_FILTER;
                    }
                }
            }
        return filter;
    }
}

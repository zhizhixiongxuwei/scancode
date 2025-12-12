/**
 * ****************************************************************************
 *  Copyright (c) 2004, 2020 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *      IBM - Initial API and implementation
 *      Anton Leherbauer (Wind River Systems)
 *      Markus Schorn (Wind River Systems)
 *      Alexander Fedorov (ArSysOp) - Bug 561992
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.dom.parser;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.internal.core.parser.ParserMessages;

/**
 * Models problems, all problems should derive from this class.
 */
public class ASTProblem extends ASTNode implements IASTProblem {

    static final public Map<Integer, String> errorMessages;

    static {
        errorMessages = new HashMap<>();
        errorMessages.put(Integer.valueOf(PREPROCESSOR_POUND_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.error"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_POUND_WARNING), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.warning"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INCLUSION_NOT_FOUND), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.inclusionNotFound"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_DEFINITION_NOT_FOUND), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.definitionNotFound"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_MACRO_DEFN), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidMacroDefn"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_MACRO_REDEFN), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidMacroRedefn"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_UNBALANCE_CONDITION), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.unbalancedConditional"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_CONDITIONAL_EVAL_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.conditionalEval"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_MACRO_USAGE_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.macroUsage"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_CIRCULAR_INCLUSION), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.circularInclusion"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_DIRECTIVE), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidDirective"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_MACRO_PASTING_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.macroPasting"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_MISSING_RPAREN_PARMLIST), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.missingRParen"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_VA_ARGS), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidVaArgs"));
        errorMessages.put(Integer.valueOf(SCANNER_INVALID_ESCAPECHAR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.invalidEscapeChar"));
        errorMessages.put(Integer.valueOf(SCANNER_UNBOUNDED_STRING), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.unboundedString"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_FLOATING_POINT), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badFloatingPoint"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_BINARY_FORMAT), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badBinaryFormat"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_HEX_FORMAT), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badHexFormat"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_OCTAL_FORMAT), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badOctalFormat"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_DECIMAL_FORMAT), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badDecimalFormat"));
        errorMessages.put(Integer.valueOf(SCANNER_ASSIGNMENT_NOT_ALLOWED), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.assignmentNotAllowed"));
        errorMessages.put(Integer.valueOf(SCANNER_DIVIDE_BY_ZERO), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.divideByZero"));
        errorMessages.put(Integer.valueOf(SCANNER_MISSING_R_PAREN), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.missingRParen"));
        errorMessages.put(Integer.valueOf(SCANNER_EXPRESSION_SYNTAX_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.expressionSyntaxError"));
        errorMessages.put(Integer.valueOf(SCANNER_ILLEGAL_IDENTIFIER), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.illegalIdentifier"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_CONDITIONAL_EXPRESSION), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badConditionalExpression"));
        errorMessages.put(Integer.valueOf(SCANNER_UNEXPECTED_EOF), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.unexpectedEOF"));
        errorMessages.put(Integer.valueOf(SCANNER_BAD_CHARACTER), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.badCharacter"));
        errorMessages.put(Integer.valueOf(SCANNER_CONSTANT_WITH_BAD_SUFFIX), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.constantWithBadSuffix"));
        errorMessages.put(Integer.valueOf(SCANNER_FLOAT_WITH_BAD_PREFIX), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.scanner.floatWithBadPrefix"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_MULTIPLE_USER_DEFINED_SUFFIXES_IN_CONCATENATION), ParserMessages.getString(//$NON-NLS-1$
        "ScannerProblemFactory.error.preproc.multipleUserDefinedLiteralSuffixesOnStringLiteral"));
        errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_USE_OUTSIDE_PREPROCESSOR_DIRECTIVE), //$NON-NLS-1$
        ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidUsageOutsidePreprocDirective"));
        errorMessages.put(Integer.valueOf(SYNTAX_ERROR), //$NON-NLS-1$
        ParserMessages.getString("ParserProblemFactory.error.syntax.syntaxError"));
        errorMessages.put(Integer.valueOf(MISSING_SEMICOLON), //$NON-NLS-1$
        ParserMessages.getString("ParserProblemFactory.error.syntax.missingSemicolon"));
        errorMessages.put(Integer.valueOf(TEMPLATE_ARGUMENT_NESTING_DEPTH_LIMIT_EXCEEDED), ParserMessages.getString(//$NON-NLS-1$
        "ParserProblemFactory.error.syntax.templateArgumentNestingDepthLimitExceeded"));
    }

    final public int id;

    final public char[] arg;

    public boolean isError;

    public IASTProblem originalProblem = null;

    public ASTProblem(IASTNode parent, ASTNodeProperty property, int id, char[] arg, boolean isError, int startNumber, int endNumber) {
        setParent(parent);
        setPropertyInParent(property);
        setOffset(startNumber);
        setLength(endNumber - startNumber);
        this.id = id;
        this.arg = arg;
        this.isError = isError;
    }

    public ASTProblem(int id, char[] arg, boolean isError) {
        this.id = id;
        this.arg = arg;
        this.isError = isError;
    }

    @Override
    public ASTProblem copy() {
        return copy(CopyStyle.withoutLocations);
    }

    @Override
    public ASTProblem copy(CopyStyle style) {
        ASTProblem copy = new ASTProblem(id, arg == null ? null : arg.clone(), isError);
        return copy(copy, style);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public boolean isWarning() {
        return !isError;
    }

    @Override
    public String getMessageWithLocation() {
        String msg = getMessage();
        char[] file = getOriginatingFileName();
        int line = getSourceLineNumber();
        Object[] args = new Object[] { msg, new String(file), Integer.valueOf(line) };
        //$NON-NLS-1$
        return ParserMessages.getFormattedString("BaseProblemFactory.problemPattern", args);
    }

    private static String getMessage(int id, String arg, IASTProblem originalProblem) {
        String msg = errorMessages.get(Integer.valueOf(id));
        if (msg == null)
            //$NON-NLS-1$
            msg = "";
        if (arg != null) {
            msg = MessageFormat.format(msg, new Object[] { arg });
        }
        if (originalProblem != null) {
            //$NON-NLS-1$
            msg = MessageFormat.format("{0}: {1}", msg, originalProblem.getMessage());
        }
        return msg;
    }

    public static String getMessage(int id, String arg) {
        return getMessage(id, arg, null);
    }

    @Override
    public String getMessage() {
        return getMessage(id, arg == null ? null : new String(arg), originalProblem);
    }

    @Override
    public boolean checkCategory(int bitmask) {
        return (id & bitmask) != 0;
    }

    @Override
    public String[] getArguments() {
        return arg == null ? new String[0] : new String[] { new String(arg) };
    }

    public char[] getArgument() {
        return arg;
    }

    @Override
    public char[] getOriginatingFileName() {
        return getContainingFilename().toCharArray();
    }

    @Override
    public int getSourceEnd() {
        final IASTFileLocation location = getFileLocation();
        if (location != null) {
            return location.getNodeOffset() + location.getNodeLength() - 1;
        }
        return INT_VALUE_NOT_PROVIDED;
    }

    @Override
    public int getSourceLineNumber() {
        final IASTFileLocation location = getFileLocation();
        if (location != null) {
            return location.getStartingLineNumber();
        }
        return INT_VALUE_NOT_PROVIDED;
    }

    @Override
    public int getSourceStart() {
        final IASTFileLocation location = getFileLocation();
        if (location != null) {
            return location.getNodeOffset();
        }
        return INT_VALUE_NOT_PROVIDED;
    }

    @Override
    public IASTProblem getOriginalProblem() {
        return originalProblem;
    }

    @Override
    public void setOriginalProblem(IASTProblem original) {
        originalProblem = original;
    }
}

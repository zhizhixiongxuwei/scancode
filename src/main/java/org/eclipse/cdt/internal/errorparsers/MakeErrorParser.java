/**
 * ****************************************************************************
 *  Copyright (c) 2005, 2015 IBM Corporation and others.
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
 *      Norbert Ploett (Siemens AG) - externalized strings
 *      Marc-Andre Laperle (Ericsson) - Bug 462036
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.errorparsers;

import java.util.regex.Matcher;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.errorparsers.AbstractErrorParser;
import org.eclipse.cdt.core.errorparsers.ErrorPattern;
import org.eclipse.core.runtime.Path;

/**
 * @deprecated replaced with {@link CWDLocator} and {@code GmakeErrorParser}
 */
@Deprecated
public class MakeErrorParser extends AbstractErrorParser {

    static final public ErrorPattern[] patterns = { new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "make\\[(.*)\\]: Entering directory [`'](.*)'", //$NON-NLS-1$
    0, //$NON-NLS-1$
    0) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            int level;
            try {
                level = Integer.valueOf(matcher.group(1)).intValue();
            } catch (NumberFormatException e) {
                level = 0;
            }
            String dir = matcher.group(2);
            /* Sometimes make screws up the output, so
					 * "leave" events can't be seen.  Double-check level
					 * here.
					 */
            int parseLevel = eoParser.getDirectoryLevel();
            for (; level < parseLevel; level++) {
                eoParser.popDirectory();
            }
            eoParser.pushDirectory(new Path(dir));
            return true;
        }
    }, new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "make\\[.*\\]: Leaving directory", //$NON-NLS-1$
    0, //$NON-NLS-1$
    0) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            eoParser.popDirectory();
            return true;
        }
    }, new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make: \\*\\*\\* \\[.*\\] Error .*)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //make [foo] Error NN
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*\\[.*\\] Error [-]{0,1}\\d*.*)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //[foo]  signal description
    // Turning off for now, bug 203269
    // This is reporting an error on the line 'make -j8 ...'
    //		new ErrorPattern("(make.*\\d+\\s+\\w+.*)", 1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) { //$NON-NLS-1$
    //			protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
    //				super.recordError(matcher, eoParser);
    //				return true;
    //			}
    //		},
    //missing separator. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*missing separator.\\s*Stop.)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //missing separator (did you mean TAB instead of 8 spaces?\\). Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*missing separator \\(did you mean TAB instead of 8 spaces?\\).\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //commands commence before first target. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*commands commence before first target.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //commands commence before first target. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*commands commence before first target.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //missing rule before commands. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*missing rule before commands.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //missing rule before commands. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*missing rule before commands.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //No rule to make target `xxx'.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*No rule to make target `.*'.)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //No rule to make target `xxx', needed by `yyy'.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*No rule to make target `.*', needed by `.*'.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //No targets specified and no makefile found. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*No targets specified and no makefile found.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //No targets. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*No targets.\\s*Stop.)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //Makefile `xxx' was not found.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*Makefile `.*' was not found.)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //Included makefile `xxx' was not found.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*Included makefile `.*' was not found.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //warning: overriding commands for target `xxx'
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*warning: overriding commands for target `.*')", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_WARNING) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return false;
        }
    }, //warning: ignoring old commands for target `xxx'
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*warning: ignoring old commands for target `.*')", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_WARNING) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return false;
        }
    }, //Circular .+ <- .+ dependency dropped.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*Circular .+ <- .+ dependency dropped.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //Recursive variable `xxx' references itself (eventually). Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*Recursive variable `.*' references itself \\(eventually\\).\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //Unterminated variable reference. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*[uU]nterminated variable reference.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //insufficient arguments to function `.*'. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*insufficient arguments to function `.*'.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //missing target pattern. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*missing target pattern.\\s*Stop.)", //$NON-NLS-1$
    1, //$NON-NLS-1$
    IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //multiple target patterns. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*multiple target patterns.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //target pattern contains no `%'. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*target pattern contains no `%'.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //mixed implicit and static pattern rules. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*mixed implicit and static pattern rules.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //mixed implicit and static pattern rules. Stop.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*mixed implicit and static pattern rules.\\s*Stop.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    }, //warning: -jN forced in submake: disabling jobserver mode.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*warning: -jN forced in submake: disabling jobserver mode.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_WARNING) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return false;
        }
    }, //warning: jobserver unavailable: using -j1. Add `+' to parent make rule.
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*warning: jobserver unavailable: using -j1. Add `+' to parent make rule.)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_WARNING) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return false;
        }
    }, //target `abc' doesn't match the target pattern
    new //$NON-NLS-1$
    ErrorPattern(//$NON-NLS-1$
    "(make.*target `.*' doesn't match the target pattern)", //$NON-NLS-1$
    1, IMarkerGenerator.SEVERITY_ERROR_RESOURCE) {

        @Override
        protected boolean recordError(Matcher matcher, ErrorParserManager eoParser) {
            super.recordError(matcher, eoParser);
            return true;
        }
    } };

    public MakeErrorParser() {
        super(patterns);
    }
}

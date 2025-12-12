/**
 * ****************************************************************************
 *   Copyright (c) 2000, 2020 IBM Corporation and others.
 *
 *   This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License 2.0
 *   which accompanies this distribution, and is available at
 *   https://www.eclipse.org/legal/epl-2.0/
 *
 *   SPDX-License-Identifier: EPL-2.0
 *
 *   Contributors:
 *      IBM Corporation - initial API and implementation
 *      Alexander Fedorov (ArSysOp) - Bug 561992
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class CDTLogWriter {

    public File logFile = null;

    public Writer log = null;

    public boolean newSession = true;

    //$NON-NLS-1$
    static final public String SESSION = "*** SESSION";

    //$NON-NLS-1$
    static final public String ENTRY = "ENTRY";

    //$NON-NLS-1$
    protected static final String SUBENTRY = "SUBENTRY";

    //$NON-NLS-1$
    protected static final String MESSAGE = "MESSAGE";

    //$NON-NLS-1$
    protected static final String STACK = "STACK";

    protected static final String LINE_SEPARATOR;

    //$NON-NLS-1$
    protected static final String TAB_STRING = "\t";

    protected static final long MAXLOG_SIZE = 10000000;

    static {
        //$NON-NLS-1$
        String s = System.getProperty("line.separator");
        //$NON-NLS-1$
        LINE_SEPARATOR = s == null ? "\n" : s;
    }

    /**
     */
    public CDTLogWriter(File log) {
        this.logFile = log;
        if (log.length() > MAXLOG_SIZE) {
            log.delete();
            //$NON-NLS-1$
            this.logFile = CCorePlugin.getDefault().getStateLocation().append(".log").toFile();
        }
        openLogFile();
    }

    protected void closeLogFile() throws IOException {
        try {
            if (log != null) {
                log.flush();
                log.close();
            }
        } finally {
            log = null;
        }
    }

    protected void openLogFile() {
        try {
            log = new BufferedWriter(//$NON-NLS-1$
            new OutputStreamWriter(new FileOutputStream(logFile.getAbsolutePath(), true), "UTF-8"));
            if (newSession) {
                writeHeader();
                newSession = false;
            }
        } catch (IOException e) {
            // there was a problem opening the log file so log to the console
            //log = logForStream(System.err);
        }
    }

    protected void writeHeader() throws IOException {
        write(SESSION);
        writeSpace();
        String date = getDate();
        write(date);
        writeSpace();
        for (int i = SESSION.length() + date.length(); i < 78; i++) {
            //$NON-NLS-1$
            write("-");
        }
        writeln();
    }

    protected String getDate() {
        try {
            //$NON-NLS-1$
            DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SS");
            return formatter.format(new Date());
        } catch (Exception e) {
            // If there were problems writing out the date, ignore and
            // continue since that shouldn't stop us from losing the rest
            // of the information
        }
        return Long.toString(System.currentTimeMillis());
    }

    protected Writer logForStream(OutputStream output) {
        try {
            //$NON-NLS-1$
            return new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return new BufferedWriter(new OutputStreamWriter(output));
        }
    }

    /**
     * Writes the given string to the log, followed by the line terminator string.
     */
    protected void writeln(String s) throws IOException {
        write(s);
        writeln();
    }

    /**
     * Shuts down the log.
     */
    public synchronized void shutdown() {
        try {
            if (logFile != null) {
                closeLogFile();
                logFile = null;
            } else {
                if (log != null) {
                    Writer old = log;
                    log = null;
                    old.flush();
                    old.close();
                }
            }
        } catch (Exception e) {
            //we've shutdown the log, so not much else we can do!
            e.printStackTrace();
        }
    }

    protected void write(Throwable throwable) throws IOException {
        if (throwable == null)
            return;
        write(STACK);
        writeSpace();
        boolean isCoreException = throwable instanceof CoreException;
        if (isCoreException)
            //$NON-NLS-1$
            writeln("1");
        else
            //$NON-NLS-1$
            writeln("0");
        throwable.printStackTrace(new PrintWriter(log));
        if (isCoreException) {
            CoreException e = (CoreException) throwable;
            write(e.getStatus(), 0);
        }
    }

    public synchronized void log(IStatus status) {
        try {
            this.write(status, 0);
        } catch (IOException e) {
        }
    }

    protected void write(IStatus status, int depth) throws IOException {
        if (depth == 0) {
            write(ENTRY);
        } else {
            write(SUBENTRY);
            writeSpace();
            write(Integer.toString(depth));
        }
        writeSpace();
        write(status.getPlugin());
        writeSpace();
        write(Integer.toString(status.getSeverity()));
        writeSpace();
        write(Integer.toString(status.getCode()));
        writeSpace();
        write(getDate());
        writeln();
        write(MESSAGE);
        writeSpace();
        writeln(status.getMessage());
        //Took out the stack dump - too much space
        //write(status.getException());
        if (status.isMultiStatus()) {
            IStatus[] children = status.getChildren();
            for (int i = 0; i < children.length; i++) {
                write(children[i], depth + 1);
            }
        }
    }

    protected void writeln() throws IOException {
        write(LINE_SEPARATOR);
    }

    protected void write(String message) throws IOException {
        if (message != null)
            log.write(message);
    }

    protected void writeSpace() throws IOException {
        //$NON-NLS-1$
        write(" ");
    }

    public synchronized void flushLog() {
        try {
            log.flush();
        } catch (IOException e) {
        }
    }
}

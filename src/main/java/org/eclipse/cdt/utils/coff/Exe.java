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
package org.eclipse.cdt.utils.coff;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.eclipse.cdt.core.CCorePlugin;

public class Exe implements AutoCloseable {

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String NL = System.getProperty("line.separator", "\n");

    public RandomAccessFile rfile;

    public ExeHeader ehdr;

    public String file;

    static public class ExeHeader {

        public final static int EXEHDRSZ = 28;

        // 00-01 "MZ" - Link file .EXE signature
        public byte[] e_signature = new byte[2];

        // 02-03 Length of EXE file modulo 512
        public short e_lastsize;

        // 04-05 Number of 512 pages (including the last page)
        public short e_nblocks;

        // 06-07 Number of relocation entries
        public short e_nreloc;

        // 08-09 Size of header in 16 byte paragraphs,
        public short e_hdrsize;

        //       occupied by "EXE" header and relo table.
        // 0A-0B Minimum paragraphs of memory allocated
        public short e_minalloc;

        // 0C-0D Maximum number of paragraphs allocated
        public short e_maxalloc;

        //       in addition to the code size
        // 0E-0F Initial SS relative to start of executable
        public short e_ss;

        // 10-11 Initial SP
        public short e_sp;

        // 12-13 Checksum (or 0) of executable
        public short e_checksum;

        // 14-15 CS:IP relative to start of executable
        public short e_ip;

        // 16-17 CS:IP relative to start of executable
        public short e_cs;

        // 18-19 Offset of relocation table;
        public short e_relocoffs;

        //       40h for new-(NE,LE,LX,W3,PE etc.) executable
        // 1A-1B Overlay number (0h = main program)
        public short e_noverlay;

        protected ExeHeader(RandomAccessFile file) throws IOException {
            this(file, file.getFilePointer());
        }

        protected ExeHeader(RandomAccessFile file, long offset) throws IOException {
            file.seek(offset);
            byte[] hdr = new byte[EXEHDRSZ];
            file.readFully(hdr);
            ReadMemoryAccess memory = new ReadMemoryAccess(hdr, true);
            commonSetup(memory);
        }

        public ExeHeader(byte[] hdr, boolean little) throws IOException {
            ReadMemoryAccess memory = new ReadMemoryAccess(hdr, true);
            commonSetup(memory);
        }

        public ExeHeader(ReadMemoryAccess memory) throws IOException {
            commonSetup(memory);
        }

        void commonSetup(ReadMemoryAccess memory) throws IOException {
            if (memory.getSize() < EXEHDRSZ) {
                //$NON-NLS-1$
                throw new IOException("Not DOS EXE format");
            }
            memory.getBytes(e_signature);
            if (e_signature[0] != 'M' || e_signature[1] != 'Z') {
                //$NON-NLS-1$
                throw new IOException(CCorePlugin.getResourceString("Util.exception.notDOSFormat"));
            }
            e_lastsize = memory.getShort();
            e_nblocks = memory.getShort();
            e_nreloc = memory.getShort();
            e_hdrsize = memory.getShort();
            e_minalloc = memory.getShort();
            e_maxalloc = memory.getShort();
            e_ss = memory.getShort();
            e_sp = memory.getShort();
            e_checksum = memory.getShort();
            e_ip = memory.getShort();
            e_cs = memory.getShort();
            e_relocoffs = memory.getShort();
            e_noverlay = memory.getShort();
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            //$NON-NLS-1$
            buffer.append("EXE HEADER VALUES").append(NL);
            //$NON-NLS-1$
            buffer.append("signature ");
            buffer.append((char) e_signature[0]).append(' ').append((char) e_signature[1]);
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("lastsize: 0x");
            buffer.append(Long.toHexString(e_lastsize));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("nblocks: 0x");
            buffer.append(Long.toHexString(e_nblocks));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("nreloc: 0x");
            buffer.append(Long.toHexString(e_nreloc));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("hdrsize: 0x");
            buffer.append(Long.toHexString(e_hdrsize));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("minalloc: 0x");
            buffer.append(Long.toHexString(e_minalloc));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("maxalloc: 0x");
            buffer.append(Long.toHexString(e_maxalloc));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("ss: 0x");
            buffer.append(Long.toHexString(e_ss));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("sp: 0x");
            buffer.append(Long.toHexString(e_sp));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("checksum: 0x");
            buffer.append(Long.toHexString(e_checksum));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("ip: 0x");
            buffer.append(Long.toHexString(e_ip));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("cs: 0x");
            buffer.append(Long.toHexString(e_cs));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("relocoffs: 0x");
            buffer.append(Long.toHexString(e_relocoffs));
            buffer.append(NL);
            //$NON-NLS-1$
            buffer.append("overlay: 0x");
            buffer.append(Long.toHexString(e_noverlay));
            buffer.append(NL);
            return buffer.toString();
        }
    }

    public ExeHeader getExeHeader() throws IOException {
        return ehdr;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(file).append(NL);
        buffer.append(ehdr);
        return buffer.toString();
    }

    public Exe(String file) throws IOException {
        this.file = file;
        //$NON-NLS-1$
        rfile = new RandomAccessFile(file, "r");
        try {
            ehdr = new ExeHeader(rfile);
        } finally {
            if (ehdr == null) {
                rfile.close();
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (rfile != null) {
            rfile.close();
            rfile = null;
            ehdr = null;
        }
    }

    public static void main(String[] args) {
        try {
            Exe exe = new Exe(args[0]);
            System.out.println(exe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

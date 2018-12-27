/* This file is part of JnaFileChooser.
 *
 * JnaFileChooser is free software: you can redistribute it and/or modify it
 * under the terms of the new BSD license.
 *
 * JnaFileChooser is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.filechooser.WindowsFileChooser.Comdlg32.Comdlg32Params;
import com.sun.jna.*;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


/**
 * The native Windows file chooser dialog.
 * <p>
 * Example:
 * WindowsFileChooser fc = new WindowsFileChooser("C:\\");
 * fc.addFilter("All Files", "*");
 * fc.addFilter("Text files", "txt", "log", "xml", "css", "html");
 * fc.addFilter("Source code", "java", "c", "cpp", "cc", "h", "hpp");
 * fc.addFilter("Binary files", "exe", "class", "jar", "dll", "so");
 * if (fc.showOpenFileDialogInternal(parent)) {
 * File f = fc.getSelectedFile();
 * // do something with f
 * }
 * <p>
 * Note that although you can set the initial directory Windows will
 * determine the initial directory according to the following rules
 * (the initial directory is referred to as "lpstrInitialDir"):
 * <p>
 * Windows 7:
 * 1. If lpstrInitialDir has the same value as was passed the first time the
 * application used an Open or Save As dialog box, the path most recently
 * selected by the user is used as the initial directory.
 * 2. Otherwise, if lpstrFile contains a path, that path is the initial
 * directory.
 * 3. Otherwise, if lpstrInitialDir is not NULL, it specifies the initial
 * directory.
 * 4. If lpstrInitialDir is NULL and the current directory contains any files of
 * the specified filter types, the initial directory is the current
 * directory.
 * 5. Otherwise, the initial directory is the personal files directory of the
 * current user.
 * 6. Otherwise, the initial directory is the Desktop folder.
 * <p>
 * Windows 2000/XP/Vista:
 * 1. If lpstrFile contains a path, that path is the initial directory.
 * 2. Otherwise, lpstrInitialDir specifies the initial directory.
 * 3. Otherwise, if the application has used an Open or Save As dialog box in
 * the past, the path most recently used is selected as the initial
 * directory. However, if an application is not run for a long time, its
 * saved selected path is discarded.
 * 4. If lpstrInitialDir is NULL and the current directory contains any files
 * of the specified filter types, the initial directory is the current
 * directory.
 * 5. Otherwise, the initial directory is the personal files directory of the
 * current user.
 * 6. Otherwise, the initial directory is the Desktop folder.
 * <p>
 * Therefore you probably want to use an exe wrapper like WinRun4J in order
 * for this to work properly on Windows 7. Otherwise multiple programs may
 * interfere with each other. Unfortunately there doesn't seem to be a way
 * to override this behaviour.
 */
public class WindowsFileChooser {
    private WindowsFileChooserProps props = new WindowsFileChooserProps();

    public WindowsFileChooser() {
    }

    public WindowsFileChooser(WindowsFileChooserProps props) {
        this.props = props;
    }

    public WindowsFileChooser(Consumer<WindowsFileChooserProps> config) {
        config.accept(props);
    }

    public static class WindowsFileChooserProps {
        private List<FileExtensionFilter> filters = new ArrayList<>();
        public Path initialFile;
        public String title;
        private Window parent;
        public Mode mode = Mode.OPEN;

        public void setGuiParent(Container component) {
            parent = SwingUtilities.getWindowAncestor(component);
        }

        /**
         * add a filter to the user-selectable list of file filters
         *
         * @param filter you must pass at least 2 arguments, the first argument
         *               is the name of this filter and the remaining arguments
         *               are the file extensions.  For example, to select only .txt files
         *               you might pass {"Text Files (*.txt)", "txt"}, note the absence of the period
         *               in the file extensions.
         */
        public void addFilter(String... filter) {
            if (filter.length < 2) {
                throw new IllegalArgumentException();
            }

            FileExtensionFilter newFilter = new FileExtensionFilter();
            newFilter.description = filter[0];
            newFilter.patterns = Arrays.asList(filter).subList(1, filter.length);
            filters.add(newFilter);
        }

        public enum Mode {
            OPEN,
            SAVE
        }
    }

    private static class FileExtensionFilter {
        String description;
        List<String> patterns = new ArrayList<>();
    }

    /**
     * @return the selected file, or null if no file selected
     */
    public Path getFile() {
        boolean isOpenMode = true;
        if (props.mode == WindowsFileChooserProps.Mode.SAVE)
            isOpenMode = false;

        return showDialog(props.parent, isOpenMode);
    }

    /**
     * shows the dialog
     *
     * @param parent the parent window
     * @param open   whether to show the open dialog, if false save dialog is shown
     * @return true if the user clicked ok, false otherwise
     */
    private Path showDialog(Window parent, boolean open) {
        final Comdlg32Params params = new Comdlg32Params();
        params.Flags =
            // use explorer-style interface
            Comdlg32.OFN_EXPLORER
                // the dialog changes the current directory when browsing,
                // this flag causes the original value to be restored after the
                // dialog returns
                | Comdlg32.OFN_NOCHANGEDIR
                // disable "open as read-only" feature
                | Comdlg32.OFN_HIDEREADONLY
                // enable resizing of the dialog
                | Comdlg32.OFN_ENABLESIZING;

        if (parent != null)
            params.hwndOwner = Native.getWindowPointer(parent);

        // lpstrFile contains the selection path after the dialog
        // returns. It must be big enough for the path to fit or
        // GetOpenFileName returns an error (FNERR_BUFFERTOOSMALL).
        // MAX_PATH is 260 so 4*260+1 bytes should be big enough (I hope...)
        // http://msdn.microsoft.com/en-us/library/aa365247.aspx#maxpath
        final int maxFileNameLength = 260;
        // 4 bytes per char + 1 null byte
        final int lpstrFileBufferLength = 4 * maxFileNameLength + 1;
        params.lpstrFile = new Memory(lpstrFileBufferLength);
        params.lpstrFile.clear(lpstrFileBufferLength);

        // nMaxFile
        // http://msdn.microsoft.com/en-us/library/ms646839.aspx:
        // "The size, in characters, of the buffer pointed to by
        // lpstrFile. The buffer must be large enough to store the
        // path and file name string or strings, including the
        // terminating NULL character."

        // Therefore because we're using the unicode version of the
        // API the nMaxFile value must be 1/4 of the lpstrFile
        // buffer size plus one for the terminating null byte.
        params.nMaxFile = maxFileNameLength;

        // build filter string if filters were specified
        if (props.filters.size() > 0) {
            params.lpstrFilter = new WString(buildFilterString());
            params.nFilterIndex = 1;
        }

        // now set the initial file or directory
        if (props.initialFile != null) {
            if (Files.isDirectory(props.initialFile)) {

                String initialDir = props.initialFile.toAbsolutePath().toString();

                int lpstrInitialDirBufferLength = 4 * initialDir.getBytes().length + 1;
                params.lpstrInitialDir = new Memory(lpstrInitialDirBufferLength);
                params.lpstrInitialDir.clear(lpstrInitialDirBufferLength);
                params.lpstrInitialDir.setWideString(0L, initialDir);
            } else {
                String initialFile = props.initialFile.toAbsolutePath().toString();
                params.lpstrFile.setWideString(0L, initialFile);
            }
        }

        if (props.title != null) {
            int buffer = 4 * props.title.getBytes().length + 1;
            params.lpstrTitle = new Memory(buffer);
            params.lpstrTitle.clear(buffer);
            params.lpstrTitle.setWideString(0L, props.title);
        }


        final boolean successful = open ?
            Comdlg32.GetOpenFileNameW(params) :
            Comdlg32.GetSaveFileNameW(params);

        if (successful) {
            final String filePath = params.lpstrFile.getString(0, true);

            // we could post process the file path based on the extension if we wanted
            // the index (1-based) is available in nFilterIndex after the dialog is closed

            Path selectedFile = Paths.get(filePath).toAbsolutePath();

            if (!open) {
                selectedFile = postProcessSelectedFileForSaving(selectedFile, params);
            }

            return selectedFile;
        } else {
            final int errCode = Comdlg32.CommDlgExtendedError();

            // if the code is 0 the user clicked cancel.  This is OK.
            if (errCode == 0) {
                return null;
            } else {
                throw new RuntimeException("GetOpenFileName failed with error " + errCode);
            }
        }
    }

    /*
     * builds a filter string
     *
     * from MSDN:
     * A buffer containing pairs of null-terminated filter strings. The last
     * string in the buffer must be terminated by two NULL characters.
     *
     * The first string in each pair is a display string that describes the
     * filter (for example, "Text Files"), and the second string specifies the
     * filter pattern (for example, "*.TXT"). To specify multiple filter
     * patterns for a single display string, use a semicolon to separate the
     * patterns (for example, "*.TXT;*.DOC;*.BAK").
     *
     * For example "All Files\0*.*\0\0"
     *
     * http://msdn.microsoft.com/en-us/library/ms646839.aspx
     */
    private String buildFilterString() {

        final StringBuilder allFiltersStr = new StringBuilder();
        for (FileExtensionFilter filter : props.filters) {
            String filterStr = filter.description + "\0" + String.join(";", filter.patterns) + "\0";
            allFiltersStr.append(filterStr);
        }

        // final terminator
        allFiltersStr.append('\0');

        return allFiltersStr.toString();
    }

    private Path postProcessSelectedFileForSaving(Path selectedFile, Comdlg32Params params) {
        if (props.filters.size() == 0)
            return selectedFile;

        FileExtensionFilter selectedFilter = getSelectedFilter(params);

        // if selected filter is not determinant, for example, "All Files (*.*)", we should not do anything.
        String patternToUse = selectedFilter.patterns.get(0);
        if (patternToUse.endsWith("*")) {
            // do nothing
            return selectedFile;
        }
        else {
            // if the file already ends with the selected extension, leave it.
            // otherwise, add the selected extension.
            // This assumes that it only makes sense to use one pattern for filters for saving files.
            // turn "*.txt" into ".txt"
            String extensionFromPattern = patternToUse.replace("*.", ".");
            String originalFileName = selectedFile.getFileName().toString();
            if (!originalFileName.endsWith(extensionFromPattern)) {
                // turn "myFile" and "*.txt" into "myFile.txt"
                String newFileName = patternToUse.replace("*.", originalFileName + ".");
                return selectedFile.getParent().resolve(newFileName);
            }
            else {
                return selectedFile;
            }
        }
    }

    private FileExtensionFilter getSelectedFilter(Comdlg32Params params) {
        return props.filters.get(params.nFilterIndex - 1);
    }

    /**
     * Interface for the native Windows dialog.
     */
    public static class Comdlg32 {
        static {
            Native.register("comdlg32");
        }

        public static native boolean GetOpenFileNameW(Comdlg32Params params);

        public static native boolean GetSaveFileNameW(Comdlg32Params params);

        public static native int CommDlgExtendedError();

        public static class Comdlg32Params extends Structure {

            public int lStructSize;
            public Pointer hwndOwner;
            public Pointer hInstance;
            public WString lpstrFilter;
            public Pointer lpstrCustomFilter;
            public int nMaxCustFilter;
            public int nFilterIndex;
            public Pointer lpstrFile;
            public int nMaxFile;
            public String lpstrFileTitle;
            public int nMaxFileTitle;
            public Pointer lpstrInitialDir;
            public Pointer lpstrTitle;
            public int Flags;
            public short nFileOffset;
            public short nFileExtension;
            public String lpstrDefExt;
            public Pointer lCustData;
            public Pointer lpfnHook;
            public Pointer lpTemplateName;

            Comdlg32Params() {
                lStructSize = size();
            }

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("lStructSize",
                    "hwndOwner", "hInstance", "lpstrFilter", "lpstrCustomFilter"
                    , "nMaxCustFilter", "nFilterIndex", "lpstrFile", "nMaxFile"
                    , "lpstrFileTitle", "nMaxFileTitle", "lpstrInitialDir", "lpstrTitle"
                    , "Flags", "nFileOffset", "nFileExtension", "lpstrDefExt"
                    , "lCustData", "lpfnHook", "lpTemplateName");
            }
        }

        // flags for the Comdlg32Params structure
        public final static int OFN_READONLY = 0x00000001;
        public final static int OFN_OVERWRITEPROMPT = 0x00000002;
        public static final int OFN_HIDEREADONLY = 0x00000004;
        public static final int OFN_NOCHANGEDIR = 0x00000008;
        public static final int OFN_SHOWHELP = 0x00000010;
        public static final int OFN_ENABLEHOOK = 0x00000020;
        public static final int OFN_ENABLETEMPLATE = 0x00000040;
        public static final int OFN_ENABLETEMPLATEHANDLE = 0x00000080;
        public static final int OFN_NOVALIDATE = 0x00000100;
        public static final int OFN_ALLOWMULTISELECT = 0x00000200;
        public static final int OFN_EXTENSIONDIFFERENT = 0x00000400;
        public static final int OFN_PATHMUSTEXIST = 0x00000800;
        public static final int OFN_FILEMUSTEXIST = 0x00001000;
        public static final int OFN_CREATEPROMPT = 0x00002000;
        public static final int OFN_SHAREAWARE = 0x00004000;
        public static final int OFN_NOREADONLYRETURN = 0x00008000;
        public static final int OFN_NOTESTFILECREATE = 0x00010000;
        public static final int OFN_NONETWORKBUTTON = 0x00020000;
        public static final int OFN_NOLONGNAMES = 0x00040000;
        public static final int OFN_EXPLORER = 0x00080000;
        public static final int OFN_NODEREFERENCELINKS = 0x00100000;
        public static final int OFN_LONGNAMES = 0x00200000;
        public static final int OFN_ENABLEINCLUDENOTIFY = 0x00400000;
        public static final int OFN_ENABLESIZING = 0x00800000;
        public static final int OFN_DONTADDTORECENT = 0x02000000;
        public static final int OFN_FORCESHOWHIDDEN = 0x10000000;

        // error codes from cderr.h which may be returned by
        // CommDlgExtendedError for the GetOpenFileName and
        // GetSaveFileName functions.
        public static final int CDERR_DIALOGFAILURE = 0xFFFF;
        public static final int CDERR_FINDRESFAILURE = 0x0006;
        public static final int CDERR_INITIALIZATION = 0x0002;
        public static final int CDERR_LOADRESFAILURE = 0x0007;
        public static final int CDERR_LOADSTRFAILURE = 0x0005;
        public static final int CDERR_LOCKRESFAILURE = 0x0008;
        public static final int CDERR_MEMALLOCFAILURE = 0x0009;
        public static final int CDERR_MEMLOCKFAILURE = 0x000A;
        public static final int CDERR_NOHINSTANCE = 0x0004;
        public static final int CDERR_NOHOOK = 0x000B;
        public static final int CDERR_NOTEMPLATE = 0x0003;
        public static final int CDERR_STRUCTSIZE = 0x0001;
        public static final int FNERR_SUBCLASSFAILURE = 0x3001;
        public static final int FNERR_INVALIDFILENAME = 0x3002;
        public static final int FNERR_BUFFERTOOSMALL = 0x3003;
    }
}

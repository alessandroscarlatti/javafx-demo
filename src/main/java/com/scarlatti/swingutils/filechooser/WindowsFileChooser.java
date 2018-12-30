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

import com.sun.jna.*;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
 * application used an Open or Save As dialog box, the file most recently
 * selected by the user is used as the initial directory.
 * 2. Otherwise, if lpstrFile contains a file, that file is the initial
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
 * 1. If lpstrFile contains a file, that file is the initial directory.
 * 2. Otherwise, lpstrInitialDir specifies the initial directory.
 * 3. Otherwise, if the application has used an Open or Save As dialog box in
 * the past, the file most recently used is selected as the initial
 * directory. However, if an application is not run for a long time, its
 * saved selected file is discarded.
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
public class WindowsFileChooser extends AbstractFileChooserWidget {
    {
        filters.add(
            FileExtensionFilter.filter("All files (*.*)", "*.*")
        );
    }

    public WindowsFileChooser() {
    }

    public WindowsFileChooser(Consumer<WindowsFileChooser> config) {
        config.accept(this);
    }

    public static WindowsFileChooser dialog() {
        return new WindowsFileChooser();
    }

    public static WindowsFileChooser dialog(Consumer<WindowsFileChooser> config) {
        return new WindowsFileChooser(config);
    }

    @Override
    public Path doChooseFile() {
        Objects.requireNonNull(mode, "Mode must not be null.");
        return showWindowsFileChooserDialog(parent, mode == Mode.OPEN);
    }

    /**
     * shows the dialog
     *
     * @param parent the parent window
     * @param open   whether to show the open dialog, if false save dialog is shown
     * @return true if the user clicked ok, false otherwise
     */
    private Path showWindowsFileChooserDialog(Window parent, boolean open) {
        final Comdlg32.OpenFileNameStructure params = new Comdlg32.OpenFileNameStructure();
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

        // lpstrFile contains the selection file after the dialog
        // returns. It must be big enough for the file to fit or
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
        // file and file name string or strings, including the
        // terminating NULL character."

        // Therefore because we're using the unicode version of the
        // API the nMaxFile value must be 1/4 of the lpstrFile
        // buffer size plus one for the terminating null byte.
        params.nMaxFile = maxFileNameLength;

        // build filter string if filters were specified
        if (filters.size() > 0) {
            params.lpstrFilter = new WString(buildFilterString());
            params.nFilterIndex = 1;
        }

        // now set the initial file or directory
        if (file != null) {
            if (Files.isDirectory(file)) {

                String initialDir = file.toAbsolutePath().toString();

                int lpstrInitialDirBufferLength = 4 * initialDir.getBytes().length + 1;
                params.lpstrInitialDir = new Memory(lpstrInitialDirBufferLength);
                params.lpstrInitialDir.clear(lpstrInitialDirBufferLength);
                params.lpstrInitialDir.setWideString(0L, initialDir);
            } else {
                params.lpstrFile.setWideString(0L, file.toAbsolutePath().toString());
            }
        }

        if (title != null) {
            int buffer = 4 * title.getBytes().length + 1;
            params.lpstrTitle = new Memory(buffer);
            params.lpstrTitle.clear(buffer);
            params.lpstrTitle.setWideString(0L, title);
        }


        final boolean successful = open ?
            Comdlg32.GetOpenFileNameW(params) :
            Comdlg32.GetSaveFileNameW(params);

        if (successful) {
            final String filePath = params.lpstrFile.getString(0, true);

            // we could post process the file file based on the extension if we wanted
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
        for (FileExtensionFilter filter : filters) {
            String filterStr = filter.getDescription() + "\0" + String.join(";", filter.getPatterns()) + "\0";
            allFiltersStr.append(filterStr);
        }

        // final terminator
        allFiltersStr.append('\0');

        return allFiltersStr.toString();
    }

    private Path postProcessSelectedFileForSaving(Path selectedFile, Comdlg32.OpenFileNameStructure params) {
        if (filters.size() == 0)
            return selectedFile;

        FileExtensionFilter selectedFilter = getSelectedFilter(params);

        // if selected filter is not determinant, for example, "All Files (*.*)", we should not do anything.
        String patternToUse = selectedFilter.getPatterns().get(0);
        if (patternToUse.endsWith("*")) {
            // do nothing
            return selectedFile;
        } else {
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
            } else {
                return selectedFile;
            }
        }
    }

    private FileExtensionFilter getSelectedFilter(Comdlg32.OpenFileNameStructure params) {
        return filters.get(params.nFilterIndex - 1);
    }

    /**
     * Interface for the native Windows dialog.
     */
    private static class Comdlg32 {
        static {
            Native.register("comdlg32");
        }

        public static native boolean GetOpenFileNameW(OpenFileNameStructure params);

        public static native boolean GetSaveFileNameW(OpenFileNameStructure params);

        public static native int CommDlgExtendedError();


        /**
         * typedef struct tagOFNA {
         * DWORD         lStructSize;
         * HWND          hwndOwner;
         * HINSTANCE     hInstance;
         * LPCSTR        lpstrFilter;
         * LPSTR         lpstrCustomFilter;
         * DWORD         nMaxCustFilter;
         * DWORD         nFilterIndex;
         * LPSTR         lpstrFile;
         * DWORD         nMaxFile;
         * LPSTR         lpstrFileTitle;
         * DWORD         nMaxFileTitle;
         * LPCSTR        lpstrInitialDir;
         * LPCSTR        lpstrTitle;
         * DWORD         Flags;
         * WORD          nFileOffset;
         * WORD          nFileExtension;
         * LPCSTR        lpstrDefExt;
         * LPARAM        lCustData;
         * LPOFNHOOKPROC lpfnHook;
         * LPCSTR        lpTemplateName;
         * LPEDITMENU    lpEditInfo;
         * LPCSTR        lpstrPrompt;
         * void          *pvReserved;
         * DWORD         dwReserved;
         * DWORD         FlagsEx;
         * } OPENFILENAMEA, *LPOPENFILENAMEA;
         */
        // must be public for JNA reflection
        public static class OpenFileNameStructure extends Structure {

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

            OpenFileNameStructure() {
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

        // flags for the OpenFileNameStructure structure
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

    private static class SomeDll {
        static {
            Native.register("user32");
        }

        /**
         * https://docs.microsoft.com/en-us/windows/desktop/api/shlobj_core/nf-shlobj_core-shbrowseforfolderw
         */
        public static native boolean SHBrowseForFolderW(BrowseInfoAStructure params);

        /**
         * typedef struct _browseinfoA {
         * HWND              hwndOwner;
         * PCIDLIST_ABSOLUTE pidlRoot;
         * LPSTR             pszDisplayName;
         * LPCSTR            lpszTitle;
         * UINT              ulFlags;
         * BFFCALLBACK       lpfn;
         * LPARAM            lParam;
         * int               iImage;
         * } BROWSEINFOA, *PBROWSEINFOA, *LPBROWSEINFOA;
         * <p>
         * https://docs.microsoft.com/en-us/windows/desktop/api/shlobj_core/ns-shlobj_core-_browseinfoa
         */
        public static class BrowseInfoAStructure extends Structure {

            public Pointer hwndOwner;
            public int pidlRoot;  // not sure about this type
            public WString pszDisplayName;
            public WString pszTitle;
            public WString lpstrFilter;
            public int ulFlags;
            public Pointer lpfn;
            public int iImage;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(
                    "hwndOwner",
                    "pidlRoot",
                    "pszDisplayName",
                    "lpszTitle",
                    "ulFlags",
                    "lpfn",
                    "lParam",
                    "iImage"
                );
            }

            // 0x00000001. Only return file system directories. If the user selects folders that are not part of the file system, the OK button is grayed.
            private static final int BIF_RETURNONLYFSDIRS = 0x00000001;

            //Note  The OK button remains enabled for "\\server" items, as well as "\\server\share" and directory items. However, if the user selects a "\\server" item, passing the PIDL returned by SHBrowseForFolder to SHGetPathFromIDList fails.

            //0x00000002. Do not include network folders below the domain level in the dialog box's tree view control.
            private static final int BIF_DONTGOBELOWDOMAIN = 0x00000002;

            //0x00000004. Include a status area in the dialog box. The callback function can set the status text by sending messages to the dialog box. This flag is not supported when BIF_NEWDIALOGSTYLE is specified.
            private static final int BIF_STATUSTEXT = 0x00000004;

            //0x00000008. Only return file system ancestors. An ancestor is a subfolder that is beneath the root folder in the namespace hierarchy. If the user selects an ancestor of the root folder that is not part of the file system, the OK button is grayed.
            private static final int BIF_RETURNFSANCESTORS = 0x00000008;

            //0x00000010. Version 4.71. Include an edit control in the browse dialog box that allows the user to type the name of an item.
            private static final int BIF_EDITBOX = 0x00000010;

            //    0x00000020. Version 4.71. If the user types an invalid name into the edit box, the browse dialog box calls the application's BrowseCallbackProc with the BFFM_VALIDATEFAILED message. This flag is ignored if BIF_EDITBOX is not specified.
            private static final int BIF_VALIDATE = 0x00000020;

            //    0x00000040. Version 5.0. Use the new user interface. Setting this flag provides the user with a larger dialog box that can be resized. The dialog box has several new capabilities, including: drag-and-drop capability within the dialog box, reordering, shortcut menus, new folders, delete, and other shortcut menu commands.
            private static final int BIF_NEWDIALOGSTYLE = 0x00000040;

            //    Note  If COM is initialized through CoInitializeEx with the COINIT_MULTITHREADED flag set, SHBrowseForFolder fails if BIF_NEWDIALOGSTYLE is passed.

            //    0x00000080. Version 5.0. The browse dialog box can display URLs. The BIF_USENEWUI and BIF_BROWSEINCLUDEFILES flags must also be set. If any of these three flags are not set, the browser dialog box rejects URLs. Even when these flags are set, the browse dialog box displays URLs only if the folder that contains the selected item supports URLs. When the folder's IShellFolder::GetAttributesOf method is called to request the selected item's attributes, the folder must set the SFGAO_FOLDER attribute flag. Otherwise, the browse dialog box will not display the URL.
            private static final int BIF_BROWSEINCLUDEURLS = 0x00000080;

            //    BIF_USENEWUI
            //    Version 5.0. Use the new user interface, including an edit box. This flag is equivalent to BIF_EDITBOX | BIF_NEWDIALOGSTYLE.
            //
            //    Note  If COM is initialized through CoInitializeEx with the COINIT_MULTITHREADED flag set, SHBrowseForFolder fails if BIF_USENEWUI is passed.

            //    0x00000100. Version 6.0. When combined with BIF_NEWDIALOGSTYLE, adds a usage hint to the dialog box, in place of the edit box. BIF_EDITBOX overrides this flag.
            private static final int BIF_UAHINT = 0x00000100;

            //    0x00000200. Version 6.0. Do not include the New Folder button in the browse dialog box.
            private static final int BIF_NONEWFOLDERBUTTON = 0x00000200;

            //    0x00000400. Version 6.0. When the selected item is a shortcut, return the PIDL of the shortcut itself rather than its target.
            private static final int BIF_NOTRANSLATETARGETS = 0x00000400;

            //    0x00001000. Only return computers. If the user selects anything other than a computer, the OK button is grayed.
            private static final int BIF_BROWSEFORCOMPUTER = 0x00001000;

            //    0x00002000. Only allow the selection of printers. If the user selects anything other than a printer, the OK button is grayed.
            private static final int BIF_BROWSEFORPRINTER = 0x00002000;

            //    In Windows XP and later systems, the best practice is to use a Windows XP-style dialog, setting the root of the dialog to the Printers and Faxes folder (CSIDL_PRINTERS).

            //    0x00004000. Version 4.71. The browse dialog box displays files as well as folders.
            private static final int BIF_BROWSEINCLUDEFILES = 0x00004000;

            //    0x00008000. Version 5.0. The browse dialog box can display sharable resources on remote systems. This is intended for applications that want to expose remote shares on a local system. The BIF_NEWDIALOGSTYLE flag must also be set.
            private static final int BIF_SHAREABLE = 0x00008000;

            //    0x00010000. Windows 7 and later. Allow folder junctions such as a library or a compressed file with a .zip file name extension to be browsed.
            private static final int BIF_BROWSEFILEJUNCTIONS = 0x00010000;
        }
    }
}

/* This file is part of JnaFileChooser.
 *
 * JnaFileChooser is free software: you can redistribute it and/or modify it
 * under the terms of the new BSD license.
 *
 * JnaFileChooser is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.scarlatti.swingutils.file;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * The native Windows folder browser.
 *
 * Example:
 * WindowsFolderBrowser fb = new WindowsFolderBrowser();
 * File dir = fb.showDialog(parentWindow);
 * if (dir != null) {
 *     // do something with dir
 * }
 */
public class WindowsFolderBrowser
{
	private Path directory;
	private String title;
	private Window parent;

	/**
	 * creates a new folder browser
	 */
	public WindowsFolderBrowser() {
		title = null;
	}

	/**
	 * creates a new folder browser with text that can be used as title
	 * or to give instructions to the user
	 *
	 * @param title text that will be displayed at the top of the dialog
	 */
	public WindowsFolderBrowser(String title) {
		this.title = title;
	}

	public Path chooseDirectory() {
		File result = showDialog(parent);
		return result == null ? null : result.toPath().toAbsolutePath();
	}

	/**
	 * displays the dialog to the user
	 *
	 * @param parent the parent window
	 *
	 * @return the selected directory or null if the user canceled the dialog
	 */
	public File showDialog(Window parent) {
		Ole32.OleInitialize(null);
		final Shell32.BrowseInfo params = new Shell32.BrowseInfo();
		params.hwndOwner = Native.getWindowPointer(parent);
		params.ulFlags =
			// disable the OK button if the user selects a virtual PIDL
			Shell32.BIF_RETURNONLYFSDIRS |
			// BIF_USENEWUI is only available as of Windows 2000/Me (Shell32.dll 5.0)
			// but I guess no one is using older versions anymore anyway right?!
			// I don't know what happens if this is executed where it's
			// not supported.
			Shell32.BIF_USENEWUI;
		if (title != null) {
			params.lpszTitle = title;
		}

		// this is really tough to work with; it wants a series of structs with file ids!
//		if (directory != null) {
//			params.pidlRoot = new Memory(1024 * 4);
//			params.pidlRoot.setWideString(0, "Computer" + "\0" + "C:\\" + "\0" + "Users" + "\0\0");
//		}

		final Pointer pidl = Shell32.SHBrowseForFolder(params);
		if (pidl != null) {
			// MAX_PATH is 260 on Windows XP x32 so 4kB should
			// be more than big enough
			final Pointer path = new Memory(1024 * 4);
			Shell32.SHGetPathFromIDListW(pidl, path);
			final String filePath = path.getString(0, true);
			final File file = new File(filePath);
			Ole32.CoTaskMemFree(pidl);
			return file;
		}
		return null;
	}

	public void setDirectory(Path directory) {
		this.directory = directory;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setParent(Window parent) {
		this.parent = parent;
	}

	public static class Ole32 {
		static {
			Native.register("ole32");
		}
		public static native Pointer OleInitialize(Pointer pvReserved);
		public static native void CoTaskMemFree(Pointer pv);
	}

	public static class Shell32
	{
		static {
			Native.register("shell32");
		}

		public static native Pointer SHBrowseForFolder(BrowseInfo params);
		public static native boolean SHGetPathFromIDListW(Pointer pidl, Pointer path);

		// http://msdn.microsoft.com/en-us/library/bb773205.aspx
		public static class BrowseInfo extends Structure {
			public Pointer hwndOwner;
			public Pointer pidlRoot;
			public String pszDisplayName;
			public String lpszTitle;
			public int ulFlags;
			public Pointer lpfn;
			public Pointer lParam;
			public int iImage;

			protected List getFieldOrder() {
				return Arrays.asList("hwndOwner","pidlRoot","pszDisplayName","lpszTitle"
					,"ulFlags","lpfn","lParam","iImage");
			}
		}

		// flags for the BrowseInfo structure
		public static final int BIF_RETURNONLYFSDIRS = 0x00000001;
		public static final int BIF_DONTGOBELOWDOMAIN = 0x00000002;
		public static final int BIF_NEWDIALOGSTYLE = 0x00000040;
		public static final int BIF_EDITBOX = 0x00000010;
		public static final int BIF_USENEWUI = BIF_EDITBOX | BIF_NEWDIALOGSTYLE;
		public static final int BIF_NONEWFOLDERBUTTON = 0x00000200;
		public static final int BIF_BROWSEINCLUDEFILES = 0x00004000;
		public static final int BIF_SHAREABLE = 0x00008000;
		public static final int BIF_BROWSEFILEJUNCTIONS = 0x00010000;
	}
}

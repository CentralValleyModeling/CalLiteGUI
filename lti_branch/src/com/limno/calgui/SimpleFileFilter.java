package com.limno.calgui;

import java.io.File;

public class SimpleFileFilter extends javax.swing.filechooser.FileFilter {
	private String fileExt;

	public SimpleFileFilter(String aFileExt) {
		fileExt = aFileExt.toLowerCase();
	}

	public boolean accept(File file) {
		// Convert to lower case before checking extension
		return (file.getName().toLowerCase().endsWith("." + fileExt) || file
				.isDirectory());
	}

	public String getDescription() {
		return fileExt.toUpperCase() + " File (*." + fileExt + ")";
	}
}

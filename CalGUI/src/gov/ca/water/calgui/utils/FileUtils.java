package gov.ca.water.calgui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class FileUtils {

	// If targetLocation does not exist, it will be created.
	/**
	 * Method copies a directory and its contents
	 * 
	 * @param sourceLocation
	 *            Path to sourcedirectory
	 * @param targetLocation
	 *            Path to target directory
	 * @param subdir
	 *            If true, copy source directory to a subdirectory under the target directory; otherwise copy source contents to
	 *            target.
	 * @throws IOException
	 */

	private static Logger log = Logger.getLogger(FileUtils.class.getName());;

	public static void copyDirectory(File sourceLocation, File targetLocation, Boolean subdir) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();

			List<String> list = new ArrayList<String>(Arrays.asList(children));
			list.removeAll(Arrays.asList(".svn"));
			children = list.toArray(new String[0]);

			if (subdir == true) {
				for (int i = 0; i < children.length; i++) {
					copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), subdir);
				}
			} else {
				for (int i = 0; i < children.length; i++) {
					copyOnlyFilesinDir(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
				}
			}

		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	// If targetLocation does not exist, it will be created.
	public static void copyOnlyFilesinDir(File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {

		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void replaceLineInFile(String filename, Integer LineNum, String newText) {
		final String NL = System.getProperty("line.separator");
		File f = new File(filename);

		Integer LineCt;

		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		String textinLine;

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			LineCt = 0;
			while (true) {
				LineCt = LineCt + 1;
				textinLine = br.readLine();
				if (LineCt == LineNum) {
					sb.append(newText + NL);
				} else {
					;
					if (textinLine == null)
						break;
					sb.append(textinLine + NL);
				}
			}

			fs.close();
			in.close();
			br.close();

		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	public static void replaceLinesInFile(String filename, Integer[] LineNum, String[] newText) {

		final String NL = System.getProperty("line.separator");

		File f = new File(filename);

		Integer LineCt;

		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		String textinLine;
		Integer n = 0;

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			LineCt = 0;
			while (true) {
				LineCt = LineCt + 1;
				textinLine = br.readLine();
				if (LineCt == LineNum[n]) {
					sb.append(newText[n] + NL);
					n = n + 1;
				} else {
					;
					if (textinLine == null)
						break;
					sb.append(textinLine + NL);
				}
			}

			fs.close();
			in.close();
			br.close();

		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	public static void replaceTextInFile(String filename, String textToReplace, String newText) {

		final String NL = System.getProperty("line.separator");
		File f = new File(filename);

		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		String textinLine;

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;
				sb.append(textinLine + NL);
			}
			int cnt1 = sb.indexOf(textToReplace);
			sb.replace(cnt1, cnt1 + textToReplace.length(), newText);

			fs.close();
			in.close();
			br.close();

		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void createNewFile(String filename) {
		File f;
		f = new File(filename);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug(e.getMessage());
			}
		}
	}

	public static void writeNewLinesInFile(String filename, String[] newText) {

		File f = new File(filename);
		final String NL = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < newText.length; i++) {
			sb.append(newText[i] + NL);
		}

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	public static StringBuffer readScenarioFile(File f) {
		StringBuffer sb = new StringBuffer();

		InputStreamReader in = null;
		BufferedReader br = null;
		String textinLine;
		final String NL = System.getProperty("line.separator");

		try {

			in = new InputStreamReader(new FileInputStream(f));
			br = new BufferedReader(in);

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;

				sb.append(textinLine).append(NL);

			}

		} catch (Exception e) {
			log.debug(e.getMessage());

		}

		finally {

			try {
				br.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug(e.getMessage());
			}

		}

		return sb;

	}

	/**
	 * Returns ArrayList containing lines from a text file such as GUI_Links2.table
	 * 
	 * @param filename
	 *            Name of file to read
	 * @return ArrayList of strings - one per line
	 */

	public static int getDaysinMonth(String mon) {
		int dayct = 0;

		if (mon.equals("Apr")) {
			dayct = 30;
		} else if (mon.equals("Jun")) {
			dayct = 30;
		} else if (mon.equals("Sep")) {
			dayct = 30;
		} else if (mon.equals("Nov")) {
			dayct = 30;
		} else if (mon.equals("Feb")) {
			dayct = 28;
		} else if (mon.equals("Jan")) {
			dayct = 31;
		} else if (mon.equals("Mar")) {
			dayct = 31;
		} else if (mon.equals("May")) {
			dayct = 31;
		} else if (mon.equals("Jul")) {
			dayct = 31;
		} else if (mon.equals("Aug")) {
			dayct = 31;
		} else if (mon.equals("Oct")) {
			dayct = 31;
		} else if (mon.equals("Dec")) {
			dayct = 31;
		}
		return dayct;

	}

	public static StringBuffer reverseStringBuffer(StringBuffer sb, String delim) {
		String[] strArray = sb.toString().split(delim);
		List result = new LinkedList();

		for (int i = 0; i < strArray.length; i++) {
			result.add(strArray[i]);
		}

		Collections.reverse(result);
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < result.size(); i++) {
			sb1.append(result.toArray()[i].toString() + "|");
		}

		sb = sb1.deleteCharAt(sb1.length() - 1);
		return sb;
	}

	public static int findInArray(String[] arr, String targ) {
		int idx = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				if (arr[i].equals(targ)) {
					idx = i;
					break;
				}
			}
		}
		return idx;
	}

	public static int copyWSIDItoLookup(String index, String where) {

		int retval = 0;
		try {
			File fs = new File(GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_cvp_sys_" + index + ".table");
			File ft = new File(where, "wsi_di_cvp_sys.table");
			FileUtils.copyDirectory(fs, ft, false);

			fs = new File(GUIUtils.defaultLookupDirectoryString() + "\\WSIDI\\wsi_di_swp_" + index + ".table");
			ft = new File(where, "wsi_di_swp.table");
			FileUtils.copyDirectory(fs, ft, false);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			log.debug(e1.getMessage());
			retval = -1;
		}
		return retval;

	}

}

/*
 * Keith Williams (G00324844)
 * 16/12/2016
 */

/**
 * Manage all resources on the server.
 */

package ie.gmit.sw.server;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Resources {
	/**
	 * @param The path to the directory where the files are stored.
	 * @return List of filenames that were stored in the given directory.
	 */
	// Adapted from http://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
	public List<String> getFileNames(String directory) {
		List<String> filenames = new ArrayList<String>();

		// If this pathname does not denote a directory
		// then listFiles() returns null
		File[] files = new File(directory).listFiles();
		
		for (File file : files) {
			if (file.isFile()) {
				filenames.add(file.getName());
			}
		}
		
		return filenames;
	}
}

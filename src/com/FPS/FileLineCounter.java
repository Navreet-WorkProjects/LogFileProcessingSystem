package com.FPS;

import java.io.File;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Map;

/**
 * Class implements runnable and reads count of lines for a particular file
 * 
 * @author Navreet
 * @version 1.0
 *
 */
public class FileLineCounter implements Runnable {

	private final File file;
	// count of lines for a given file
	private int count;
	// static hashtable to maintain count of files for all files
	static Map<String, Integer> lineCount = new Hashtable<String, Integer>();

	// constructor takes file as input
	public FileLineCounter(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		try {
			// getting count of files
			count = (int) Files.newBufferedReader(file.toPath()).lines()
					.count();
			// updating hashtable with current file and its line count
			lineCount.put(file.getName(), count);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

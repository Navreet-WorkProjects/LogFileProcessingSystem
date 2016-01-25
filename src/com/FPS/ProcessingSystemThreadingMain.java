package com.FPS;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class which processes log files to prepend line numbers
 * 
 * @author Navreet Kaur
 * @version 1.0
 */
public class ProcessingSystemThreadingMain implements Constants{
	private static Scanner scanner = new Scanner(System.in);
	// Map to keep track of starting line numbers
	private static Map<String, Integer> lineCountMap = new HashMap<String, Integer>();

	/**
	 * Main method which asks for directory and number of threads
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// user enters directory of log files
			System.out.println(ENTER_DIR);
			String directory = scanner.nextLine();
			
			// user enters the number of desired threads
			System.out.println(ENTER_THREAD);
			int threads = Integer.parseInt(scanner.nextLine());
			long startTime = System.currentTimeMillis();
			
			// Calling method to process files
			processLogFiles(directory, threads);
			
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("success: Time in ms - " + totalTime);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method for filtering files with incorrect names
	 */
	public static FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String[] fileNames = name.split("[.]");
			// check if filename is in correct format
			if (fileNames == null || fileNames.length != 3) {
				return false;
			}
			return (isValidDate(fileNames[1]) && name.endsWith(FILE_END) && name
					.startsWith(FILE_START));
		}
	};

	/**
	 * Method called to read and update files
	 * 
	 * @param directory
	 *            list of file names in the folder
	 * @param threads
	 *            number of threads to be maintained
	 */
	public static String processLogFiles(String directory, int threads) {
		try {
			// Retrieving list of files in the directory and
			// filtering the files not matching the format
			File folder = new File(directory);
			File[] listOfFiles = folder.listFiles(filter);
			// sorting based on timestamp
			Arrays.sort(listOfFiles, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.compare(f1.lastModified(), f2.lastModified());
				}
			});
			// updating start line number of first file
			lineCountMap.put(listOfFiles[0].getName(), 1);
			// ExecutorService object to handle thread pool for getting count of
			// line numbers
			ExecutorService exec = Executors.newFixedThreadPool(threads);
			for (File file : listOfFiles) {
				// counting number of lines of each file in parallel
				exec.submit(new FileLineCounter(file));
			}
			exec.shutdown();
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			// Updating starting line number for all files
			for (int i = 1; i < listOfFiles.length; i++) {
				int count = FileLineCounter.lineCount.get(listOfFiles[i - 1]
						.getName());
				int count1 = lineCountMap.get(listOfFiles[i - 1].getName());
				// starting line number for current file is the sum of the
				// previous file total lines and start line number
				lineCountMap.put(listOfFiles[i].getName(), count + count1);
			}
			// ExecutorService object to handle thread pool for updating line
			// numbers
			ExecutorService execWrite = Executors.newFixedThreadPool(threads);
			for (File file : listOfFiles) {
				// passing file and its starting line number
				execWrite.submit(new FileReadWrite(file, lineCountMap.get(file
						.getName())));
			}
			execWrite.shutdown();
			execWrite.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			return SUCCESS;
		} catch (InterruptedException ie) {
			System.out.println(ie.getMessage());
			return FAIL;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return FAIL;
		}

	}

	/**
	 * Method validates if a date is valid or not
	 * 
	 * @param dateString
	 * @return
	 */
	public static boolean isValidDate(String dateString) {
		// passing format and parsing the string to date
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);
		try {
			df.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}

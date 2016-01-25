package com.FPS;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Class implements Runnable
 * Reads and updates file with line numbers
 * @author Navreet
 * @version 1.0
 *
 */
public class FileReadWrite implements Runnable,Constants {
	private final File file;
	// start line number for the file
	private int startCount;
	// constructor takes file and startcount as input
	public FileReadWrite(File file, int startCount) {
		this.file = file;
		this.startCount = startCount;
	}

	@Override
	public void run() {
		try {
			final Charset charset = StandardCharsets.UTF_8;
			final String lineSeparator = System.lineSeparator();
			//converting file to Path
			final Path init = file.toPath();
			//BufferedWriter updates the file
			final BufferedWriter writer = Files.newBufferedWriter(init, charset,
					StandardOpenOption.CREATE);
			for (final String line : Files.readAllLines(init, charset))
				writer.write(String.format(LINE_NUMBER_FORMAT, startCount++, line,
						lineSeparator));
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

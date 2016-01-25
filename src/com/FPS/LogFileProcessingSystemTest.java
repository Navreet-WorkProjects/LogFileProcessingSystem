package com.FPS;


import static org.junit.Assert.*;

import org.junit.Test;

import com.FPS.Constants;
import com.FPS.ProcessingSystemThreadingMain;


public class LogFileProcessingSystemTest implements Constants{

	@Test
	public void test() {
		String directory = DIRECTORY;
		int threads = THREADS;
		assertEquals(SUCCESS, ProcessingSystemThreadingMain.processLogFiles(directory, threads));
	}

}

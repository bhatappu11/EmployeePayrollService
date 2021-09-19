package com.bridgelabz.employeepayrollservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class NIOFileAPITest {
	private static String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_NIO="TempPlayGround";
	
    @Test 
    public void givenPath_performFileOperations_Confirm() {
    	Path homePath=Paths.get(HOME);
        Assert.assertTrue(Files.exists(homePath));
        
        Path playPath=Paths.get(HOME+"/"+PLAY_WITH_NIO);
        if(Files.exists(playPath))
        	FileUtils.deleteFiles(playPath.toFile());
        Assert.assertTrue(Files.notExists(playPath));
        
        try {
			Files.createDirectories(playPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        Assert.assertTrue(Files.exists(playPath));
		
		IntStream.range(1,10).forEach(cntr -> {
			Path tempFile = Paths.get(playPath+"/temp"+cntr);
			Assert.assertTrue(Files.notExists(tempFile));
			try { Files.createFile(tempFile);}
			catch(IOException e) {}
			Assert.assertTrue(Files.exists(tempFile));
		});

    }
}

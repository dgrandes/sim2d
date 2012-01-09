package com.pf.simulator.log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;


public class LogUtil {

	static LogUtil instance;
	Scanner scanner;
	Writer writer;
	String line;
	float currentTime;
	boolean isReading = false;
	
	static public LogUtil getInstance() {
		if(instance == null) {
			instance = new LogUtil();
		}
		
		return instance;
	}
	
	private LogUtil() {
		
	}
	
	public void loadFileForReading(String filename) {
		try {
			isReading = true;
			scanner = new Scanner(new File(filename), "UTF-8");
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		}
	}
	
	public void loadFileForWriting(String filename) {
		
		try {
			isReading = false;
			File file = new File(filename);
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(file));
		} catch (Exception e) {
			System.err.println("bad file");
		}
	}
	public void closeFile() {
		try {
			if(scanner != null) {
				scanner.close();
				scanner = null;
			}
			
			if(writer != null) {
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public State readState() {
		if (!isReading) {
			return null;
		}
		
		State state;
		StringBuffer buffer = new StringBuffer();
		float lineTime;
		
		if(!scanner.hasNextLine()) {
			return null;
		}
		
		if (line != null) {
			buffer.append(line + "\n");
			//System.out.println("linetime " + line.substring(0,line.indexOf("|")));
			lineTime = Float.parseFloat(line.substring(0,line.indexOf("|")));
		} else {
			lineTime = -1;
		}
		
		currentTime = lineTime;
		
		while (scanner.hasNextLine() && currentTime == lineTime) {
			line = scanner.nextLine();
			//System.out.println("currenttime=" + currentTime + " state=" + line);
			lineTime = Float.parseFloat(line.substring(0,line.indexOf("|")));
			
			if (currentTime == lineTime) {
				buffer.append(line + "\n");
			}
		}
		
		if (buffer.length() > 0) {
			state = State.StringToState(buffer.toString());
			return state;
		} else {
			return null;
		}
	}
	
	public void writeState(State state) {
		if(isReading) {
			return;
		}
		
		try {

			writer.write(state.StateToString());
		} catch (IOException e) {
			System.err.println("bad write");
		}
	}
	
	
}

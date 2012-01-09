package com.pf.logging;



public interface IBaseLogger {

	//Creates the buffered output writer, prompts user for name if necessary
	public void openLog() throws Exception;
	//Called when a simulation step has been done
	public void writeState() throws Exception;
	//Close log files and display messages of succes or failure
	public void closeLog() throws Exception;
	
	

}

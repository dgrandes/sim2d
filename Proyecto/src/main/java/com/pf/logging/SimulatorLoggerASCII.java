package com.pf.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.pf.model.Agent;
import com.pf.simulator.SimEnvironment;
import com.pf.simulator.Simulator;
import com.pf.simulator.log.LogReference;
import com.pf.simulator.log.State;


public class SimulatorLoggerASCII implements ISimulatorLogger {

	Writer referenceWriter;
	Writer stepsWriter;
	String refFilename;
	String stepsFilename;
	SimEnvironment env;

	public boolean binaryFormat = false;
	public LogReference envReference = new LogReference();
	public List<State> stateList = new ArrayList<State>();
	public int stateListBufferToDisc = 1024;
	@Override
	public void openLog() throws Exception{
		// TODO Auto-generated method stub
		env = Simulator.getSimEnvironment();
		envReference.setEnvReference(env.getEnvironment());
		System.out.println("opening simulator log");
		String filename = Simulator.getGuiFrame().promptUserForFileName();
		if(filename != null)
		{
			//Get the reference ans steps filenames
			 refFilename = getModdedFilename(filename, "-REF");
			 stepsFilename = getModdedFilename(filename, "-STEP");
			File rfile = new File(refFilename);	
			File sfile = new File(stepsFilename);

			try {
				if(!rfile.exists()) {
					rfile.createNewFile();
				}
				if(!sfile.exists()){
					sfile.createNewFile();
				}
				
				referenceWriter = new BufferedWriter(new FileWriter(rfile));
				stepsWriter = new BufferedWriter(new FileWriter(sfile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("Bad file");
			}
			for(Agent a : env.getAgentManager().getAgents())
			{
				putNewAgent(a, 0);
			}
		}
		else
			throw new Exception("cancel");

	}

	private String getModdedFilename(String filename, String suffix) {

		int dot = filename.lastIndexOf(".");

		if (dot == -1) {
			// System.out.println(filename+";dot:"+dot);
			dot = filename.length();
		}
		//String ext = filename.substring(dot);
		String ext = ".txt";
		String refname = filename.substring(0, dot);
		refname = refname.concat(suffix);
		refname = refname.concat(ext);
		return refname;

	}

	//Whenever this is called, it stores a new state of the simulation
	@Override
	public void writeState() throws Exception {

		List<Agent> newAgents = env.getAgentManager().getAgents();
		float time = Simulator.simulation.getCurrentStep() * env.getEnvironment().deltaTime;
		
		stateList.add(new State(newAgents, time));
		
		if(stateList.size() > stateListBufferToDisc)
		{
			for(State state: stateList)
				stepsWriter.write(state.StateToString());
			stateList.clear();
		}
		
	}

	public void putNewAgent(Agent agent, float time) {
		envReference.putNewAgent(agent, time);
	}
	
	public void agentDied(Agent agent, float time) {
		envReference.agentDied(agent, time);
	}
	@Override
	public void closeLog() throws Exception{
		// TODO Auto-generated method stub
		System.out.println("closing simulator log");
		
		//Save the reference file in the end since its not that big
		String refJSON = new Gson().toJson(envReference);
		referenceWriter.write(refJSON);
		if(stateList.size() != 0)
		{
			for(State state: stateList)
				stepsWriter.write(state.StateToString());
			
		}
		stepsWriter.close();
		referenceWriter.close();
		String sep =  File.separator;
		int nameindex = refFilename.lastIndexOf(sep);
		
		String rname, sname;
		
		rname = refFilename.substring(nameindex+1,refFilename.length());
		nameindex = stepsFilename.lastIndexOf(sep);
		sname = stepsFilename.substring(nameindex+1,stepsFilename.length());
		Simulator.getGuiFrame().showConfirmation("The REFERENCE file: "+rname
				+"\nand the STEPS file: "+sname+"\nhave been sucessfully saved!","Save succesful!");
		
	}

}

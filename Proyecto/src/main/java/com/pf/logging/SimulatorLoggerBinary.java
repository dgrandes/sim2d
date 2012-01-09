package com.pf.logging;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.pf.gui.GUIFrame;
import com.pf.model.Agent;
import com.pf.simulator.SimEnvironment;
import com.pf.simulator.Simulator;
import com.pf.simulator.log.LogReference;
import com.pf.simulator.log.State;

public class SimulatorLoggerBinary implements ISimulatorLogger {

	DataOutputStream referenceWriter;
	DataOutputStream stepsWriter;
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
		GUIFrame g = Simulator.getGuiFrame();
		String filename = g.promptUserForFileName();
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
				if(Simulator.guiFrame.menu.compressData.getState())
				{
					referenceWriter = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(rfile)));
					stepsWriter = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(sfile)));
				}
				else
				{
					referenceWriter = new DataOutputStream(new FileOutputStream(rfile));
					stepsWriter = new DataOutputStream(new FileOutputStream(sfile));
				}
					
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
		String ext = "";
		if(Simulator.guiFrame.menu.compressData.getState())
			ext = ".zbin";
		else
			ext = ".bin";
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
				writeStateBinary(state);
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
		
		int offset = 0;
		int blocksize = 1024;

	    int remaining  = refJSON.length();
		while(remaining > 0)
		{
			
			String window = "";
			
			if(remaining >= blocksize)
				window = refJSON.substring(offset, offset + blocksize);
			else
				window = refJSON.substring(offset);
		
			try{
				referenceWriter.writeUTF(window);
				offset += window.length();
				remaining = refJSON.length() - offset;
				
			}catch(UTFDataFormatException e)
			{
				System.err.println("UTF Blocksize too big");
				System.err.println( e.getMessage());
				e.printStackTrace();
						
			}
		}
		if(stateList.size() != 0)
		{
			for(State state: stateList)
			{
				writeStateBinary(state);
				
			}
			
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
	

	private void writeStateBinary(State state) throws Exception
	{
		ArrayList<ArrayList<Object>> stateList = state.getStateParameters();
	
		for(ArrayList<Object> elem : stateList)
		{

			int timeValue,agentkey;
			float px,py,vx,vy;
			timeValue = (Integer)elem.get(0);
			agentkey = (Integer)elem.get(1);
			px = (Float)elem.get(2);
			py = (Float)elem.get(3);
			vx = (Float)elem.get(4);
			vy = (Float)elem.get(5);
			ByteBuffer buffer = ByteBuffer.allocate(24);
			buffer.putInt(timeValue);
			
			stepsWriter.writeInt(timeValue);
			stepsWriter.writeInt(agentkey);
			stepsWriter.writeFloat(px);
			stepsWriter.writeFloat(py);
			stepsWriter.writeFloat(vx);
			stepsWriter.writeFloat(vy);
			
			
		}

	}

}

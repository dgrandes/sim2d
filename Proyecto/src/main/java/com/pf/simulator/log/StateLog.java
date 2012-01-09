package com.pf.simulator.log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.simulator.Simulator;

public class StateLog {

	public LogReference reference;
	public List<State> stateList;
	private transient static StateLog instance;
	private transient int curtime = -1;


	private StateLog() {
		reference = new LogReference();
		stateList = new ArrayList<State>();

	}
	
	public static StateLog get() {
		if(instance == null) {
			instance = new StateLog();
		}
		return instance;
	}

	public void putNewAgent(Agent agent, float time) {
		reference.putNewAgent(agent, time);
	}
	
	public void agentDied(Agent agent, float time) {
		reference.agentDied(agent, time);
	}

	public void reset() {
		reference = new LogReference();
		stateList = new ArrayList<State>();
	}
	/*
	public void recordState(List<Agent> newAgents, float time) {
		if(active)
		{
			stateList.add(new State(newAgents, time));
			if(!reference.envReferenceSet())
				try {
					reference.setEnvReference(Simulator.getSimEnvironment().getEnvironment());
				} catch (Exception e) {
					return;
				}
		}
	}

	public void SaveLog(String filename)
	{
		String refJSON = new Gson().toJson(reference);
		
		
		int dot = filename.lastIndexOf(".");
		
		if(dot == -1)
		{
//			System.out.println(filename+";dot:"+dot);
			dot = filename.length();
		}
		String ext = filename.substring(dot);
		String refname = filename.substring(0, dot);
		refname = refname.concat("-Reference");
		refname = refname.concat(ext);
		
		String stepname = filename.substring(0,dot);
		stepname = stepname.concat("-Steps");
		stepname = stepname.concat(ext);
		
		try {
			FileWriter fstream = new FileWriter(refname);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(refJSON);
			out.close();
			
			saveStates(stepname);
			
		} catch (Exception e) {
			System.err.println("StateLog [saveToFile] : " + e.getMessage());
			Simulator.getGuiFrame().showError("Couldnt Save the Following Files:"+stepname+", "+refname+"!", "Error during Save");
		}
		
		Simulator.getGuiFrame().showConfirmation("Steps Log File: "+stepname+"\n" +
				"Reference Log File: "+refname +"\nwere Saved Sucessfully!","Simulation Saved!" );
		
	}
	
	private void saveStates(String path)
	{
		LogUtil log = LogUtil.getInstance();
		log.loadFileForWriting(path);
		for(State s : stateList)
		{
			log.writeState(s);
			
		}
		log.closeFile();
	}
	*/
	
	private void loadReferenceFileBinBase(DataInputStream dis) throws Exception
	{

			boolean finished = false;
			StringBuilder sb = new StringBuilder();
			while(!finished)
			{
				try{
					sb.append(dis.readUTF());
				}catch(EOFException eofex)
				{
					finished = true;
				}
			}
			String refString = sb.toString();
			System.out.println(refString);
			reference = new Gson().fromJson(refString.toString(), LogReference.class);
			int agentqty = reference.getAgentQty();
			
			Simulator.getGuiFrame().showConfirmation("Environment with "+agentqty+" Agents were loaded!","Reference File Loaded!" );
			return;
		

		
	}
	private void loadReferenceFileCompressedBinary(String filename) throws Exception
	{
		try{
			DataInputStream dis = new DataInputStream(new GZIPInputStream( new FileInputStream(filename)));
			loadReferenceFileBinBase(dis);
		}catch(Exception e)
		{
			reference = null;
			System.err.println("StateLog [LoadReference] : ");
			e.printStackTrace();
			Simulator.getGuiFrame().showError("Reference File is Invalid!", "Loading Reference ZBIN");
			throw new Exception();
		}
	}
	private void loadReferenceFileBinary(String filename) throws Exception
	{
		try{
			DataInputStream dis = new DataInputStream(new FileInputStream(filename));
			loadReferenceFileBinBase(dis);
		
		}catch(Exception e)
		{
			reference = null;
			System.err.println("StateLog [LoadReference] : ");
			e.printStackTrace();
			Simulator.getGuiFrame().showError("Reference File is Invalid!", "Loading Reference BIN");
			throw new Exception();
		}
		
	}
	
	private void loadReferenceFileASCII(String filename) throws Exception
	{
		StringBuilder agentsJSON = new StringBuilder();
		String read;
		
		try{
			FileReader fstream;
			fstream = new FileReader(filename);
			BufferedReader in = new BufferedReader(fstream);
			while((read = in.readLine()) != null)
			{
				agentsJSON.append(read);
			}
			reference = new Gson().fromJson(agentsJSON.toString(), LogReference.class);
			int agentqty = reference.getAgentQty();
			
			Simulator.getGuiFrame().showConfirmation("Environment with "+agentqty+" Agents were loaded!","Reference File Loaded!" );
			return;
		}catch(Exception e)
		{
			reference = null;
			System.err.println("StateLog [LoadReference] : ");
			e.printStackTrace();
			Simulator.getGuiFrame().showError("Reference File is Invalid!", "Loading Reference");
			throw new Exception();
		}
	}
	public void loadReferenceFile(String filename) throws Exception
	{
		
		int dot = filename.lastIndexOf(".");
		
		if(dot == -1)
		{
//			System.out.println(filename+";dot:"+dot);
			dot = filename.length();
		}
		String ext = filename.substring(dot);
		if(ext.equals(".txt"))
			loadReferenceFileASCII(filename);
		else if(ext.equals(".bin"))
			loadReferenceFileBinary(filename);
		else if(ext.equals(".zbin"))
			loadReferenceFileCompressedBinary(filename);
		else
			Simulator.getGuiFrame().showError("Unknown Reference File Extension, " +
					"it should '.bin' or '.txt'", "Loading Error");
	}
	
	private void loadStepsFileASCII(String filename) throws Exception
	{
		stateList = new ArrayList<State>();
		LogUtil log = LogUtil.getInstance();
		log.loadFileForReading(filename);
		try
		{
			log.readState();
		}catch(Exception ex)
		{
			Simulator.getGuiFrame().showError("Step File Invalid!", "Loading Step File");
			throw new Exception("No states where loaded");
		}
		State s = null;
		do
		{
			s = log.readState();
			if(s != null)
				stateList.add(s);
		}while(s != null);
		if(stateList.size() == 0 )
		{
			stateList = null;
			Simulator.getGuiFrame().showError("Couldn't load a single state!", "Loading Step File");
			throw new Exception("No states where loaded");
		}
		Simulator.getGuiFrame().showConfirmation("A replay of "+stateList.size()+" steps was loaded!","Steps File Loaded!" );
	}
	
	private void loadStepsFileCompressedBinary(String filename) throws Exception
	{
		DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(filename)));
		curtime = -1;
		stateList = new ArrayList<State>();
		try{
			while(true)
			{
				stateList.add(getState(dis, true));
			}
		}catch(EOFException e)
		{
			if(stateList.size() == 0 )
			{
				stateList = null;
				Simulator.getGuiFrame().showError("Couldn't load a single state!", "Loading Step File");
				throw new Exception("No states where loaded");
			}
			Simulator.getGuiFrame().showConfirmation("A replay of "+stateList.size()+" steps was loaded!","Steps File Loaded!" );
		}
	}
	private void loadStepsFileBinary(String filename) throws Exception
	{
		DataInputStream dis = new DataInputStream(new FileInputStream(filename));
		curtime = -1;
		stateList = new ArrayList<State>();
		try{
			while(true)
			{
				stateList.add(getState(dis, false));
			}
		}catch(EOFException e)
		{
			if(stateList.size() == 0 )
			{
				stateList = null;
				Simulator.getGuiFrame().showError("Couldn't load a single state!", "Loading Step File");
				throw new Exception("No states where loaded");
			}
			Simulator.getGuiFrame().showConfirmation("A replay of "+stateList.size()+" steps was loaded!","Steps File Loaded!" );
		}
	}
	
	private State getState(DataInputStream dis, boolean compressed) throws Exception
	{
		State state = null;
		try{
			if(curtime == -1 )
				curtime = dis.readInt();
			state = new State();
			int linetime = curtime;
			while(curtime == linetime)
			{
				state.time = (float)curtime;
				AgentPositioningData agent = new AgentPositioningData();
				
				if(!compressed)
				{
					byte[] bytes = new byte[20];
					dis.read(bytes, 0, 20);
					
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					agent.id = buffer.getInt();
					agent.position = new Vector2(buffer.getFloat(),buffer.getFloat());
					agent.velocity = new Vector2(buffer.getFloat(),buffer.getFloat());
				}
				else
				{
					agent.id = dis.readInt();
					agent.position = new Vector2(dis.readFloat(),dis.readFloat());
					agent.velocity = new Vector2(dis.readFloat(),dis.readFloat());
					
				}
				state.agentsState.put(agent.id, agent);
				curtime = dis.readInt();
			}
		}catch(EOFException e)
		{
			curtime = -1;
			if(state != null)
				return state;
			else
				throw new EOFException();
		}
		
		return state;
	}
	
	public void loadStepsFile(String filename) throws Exception
	{
		int dot = filename.lastIndexOf(".");
		
		if(dot == -1)
		{
//			System.out.println(filename+";dot:"+dot);
			dot = filename.length();
		}
		String ext = filename.substring(dot);
		if(ext.equals(".txt"))
			loadStepsFileASCII(filename);
		else if(ext.equals(".bin"))
			loadStepsFileBinary(filename);
		else if (ext.equals(".zbin"))
			loadStepsFileCompressedBinary(filename);
		else
			Simulator.getGuiFrame().showError("Unknown Steps File Extension, " +
					"it should be '.bin' or .'txt'!", "Loading Error");
	}
}

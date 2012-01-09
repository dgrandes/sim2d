package com.pf.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.pf.gui.GUIFrame;
import com.pf.model.Agent;
import com.pf.movement.qlearning.QLearningBaseMovement;
import com.pf.movement.qlearning.QLearningMovement;
import com.pf.movement.qlearning.QlearningTuple;
import com.pf.simulator.Simulator;

public class QLearningLogger implements IBaseLogger {

	private Agent agent;
	private Writer writer;
	private  String absfilename;
	private String filename;
	private String directory;
	private QLearningBaseMovement mvt;
	private boolean paramsPrinted = false;
	public QLearningLogger(Agent a)
	{
		this.agent = a;
		//I know this to be true, cause the agent that has this logger, has this mvt
		mvt = (QLearningBaseMovement) agent.movement;
		paramsPrinted = false;
	
	
	}
	@Override
	public void openLog() throws Exception{
		System.out.println("opening qlearning log");
		FileWriter fstream = null;
		String sep =  File.separator;
		GUIFrame g = Simulator.getGuiFrame();
		filename = g.fileStem+"-Q("+agent.id+")"+ ".log";
		absfilename = g.baseDirectory+sep+filename;

		fstream = new FileWriter(absfilename);

		writer = new BufferedWriter(fstream);
	
	}

	@Override
	public void writeState() throws Exception{
		
		if(!paramsPrinted && writer != null)
		{
			printSimParameters();
			writer.write("\n-----SIMULATION STEPS------\nSteps | Exploration Rate | QMatrix Size | Crashes | Victories | Distance Moved | Speed\n");
			paramsPrinted = true;
		}
		
		if(writer != null && mvt.counter == 0 )
		{
			writer.write(mvt.steps + " " + mvt.explorationRate  + " " + mvt.qMatrix.size()
					+ " " + mvt.crashes + " " + mvt.victory + " "+mvt.distanceMoved+" "+mvt.agentInstantSpeed.get(mvt.agentInstantSpeed.size()-1)+ "\n");

		
		}
		if(mvt.counter ==  0)
			Simulator.getGuiFrame()
				.updateProgressBar(mvt.steps / QLearningMovement.ITERATION_QTY);
	}

	@Override
	public void closeLog() throws Exception{
		// TODO Auto-generated method stub
		paramsPrinted = false;
		System.out.println("closing qlearning log");
		writer.write("\n----QMATRIX: [Sensed Threat] > ACTION == UTILITY------\n");
		printQMatrix();
		writer.close();
		Simulator.getGuiFrame().removeProgressBar();
		Simulator.getGuiFrame().showConfirmation(
				"QLearning LOG FILE: "+filename+"\nhas been saved succesfully!",
				"Save Succesful");
		

	}
	
	private void printSimParameters() throws IOException
	{
		writer.write("-----Simulation Parameters-----\n");
		writer.write("Iteration Length: "+QLearningMovement.ITERATION_LENGTH+"\n");
		writer.write("Iteration Qty: "+QLearningMovement.ITERATION_QTY+"\n");
		writer.write("Learning Rate: "+QLearningMovement.learningRate+"\n");
		writer.write("Discount Factor: "+QLearningMovement.discountFactor+"\n");
		writer.write("Decay: "+QLearningMovement.decay+"\n");
		writer.write("Max View Distance: "+QLearningMovement.maxViewDistance+"\n");
		
	}
	private void printQMatrix() throws IOException {
		
		for (QlearningTuple qLearningTuple : mvt.qMatrix.keySet()) {
			writer.write(qLearningTuple.state + " > " + qLearningTuple.action
					+ " == " + mvt.qMatrix.get(qLearningTuple) + "\n");
		}
	}
	
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public Writer getWriter() {
		return writer;
	}
	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public QLearningBaseMovement getMvt() {
		return mvt;
	}
	public void setMvt(QLearningMovement mvt) {
		this.mvt = mvt;
	}
	
	public void logVictory() {
		mvt.victory++;
		
	}

}

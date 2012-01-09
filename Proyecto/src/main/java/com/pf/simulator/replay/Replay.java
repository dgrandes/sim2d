package com.pf.simulator.replay;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pf.gui.GuiStates;
import com.pf.manager.EnvironmentManager;
import com.pf.math.Vector2;
import com.pf.simulator.Simulator;
import com.pf.simulator.log.AgentSoftReference;
import com.pf.simulator.log.State;
import com.pf.simulator.log.StateLog;

public class Replay {


	private int currentStep;

	private int speed = 1;
	public boolean renderReplayFrame =false;
	public int frameJump = 0;
	public boolean frameJumping = false;
	
	int FRAMESTAY = 10;
	int framesPassed = 0;

	List<ReplayShape> agentShapes;
	EnvironmentManager envManager;
	Map<Integer, AgentSoftReference> agentsRef;


	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
		System.out.println("Step changed to "+currentStep);
	}
	
	public int getPercProgress()
	{
		float perc =  currentStep / (float)howManySteps();

		return (int)(perc * 100.0f);
	}
	
	public void jumpToFrame(int frame)
	{

		frameJumping = true;
		frameJump = frame;
		if(Simulator.guiFrame.state.getCurrentState() != GuiStates.replay_playing)
		{
			Simulator.guiFrame.state.changeState(GuiStates.replay_loaded);
			Simulator.renderReplayFrame(frame);
			Simulator.runReplay();
			//Simulator.guiFrame.state.changeState(GuiStates.replay_paused);
			
		}
		
	}
	
	public void setReplayStepWithPercentange(int percentage)
	{
		float perc =  percentage / 100.0f;
		if(perc < 0)
			perc = 0.0f;
		if(perc >= 1.0f)
			perc = 1.0f;
		int current = (int)(perc * howManySteps());
		if(current >= howManySteps())
			current =  howManySteps() - 1;
		System.out.println("jumping to another step");
		jumpToFrame(current);
		
		
		
	}
	public int howManySteps()
	{
		return StateLog.get().stateList.size();
	}

	public Replay(){        
		

		Reset();
	}
	

	public void Reset()
	{
		currentStep = 0;
		envManager = StateLog.get().reference.getEnvReference();
		agentShapes = new ArrayList<ReplayShape>(); 
		agentsRef = StateLog.get().reference.getAgentsReference();
	}
	
	//returns true if the frame rendered is a limit frame, ie its at the start or end.
	public boolean replayStep(int offset) {
		
		boolean limitFrame = false;
		double val;
		if(frameJumping)
		{
			val = frameJump;
		}
		else
			val = currentStep + (double)offset * (float)speed;
		
		int step;
		if(  val < 0 )
		{
			limitFrame = true;
			step = 0;
		}
		else if(val > StateLog.get().stateList.size())
		{
			limitFrame = true;
			step = StateLog.get().stateList.size() -1 ;
		}
		else
			step = (int)val;

		
	//	System.err.println("replaying frame "+step);
		List<State> states= StateLog.get().stateList;
		State currState = null;
		try
		{
			 currState = states.get(step);
			
		}catch(Exception e)
		{
			System.err.println("couldnt get step "+step);
			return true;
		}
		
	//	System.err.println("clearing shapes, going to make #new:"+currState.agentsState.size());
		agentShapes.clear();

		for(Integer i : currState.agentsState.keySet())
		{
			Vector2 position = currState.agentsState.get(i).position;
			float radius = agentsRef.get(i).radius;

			Ellipse2D ell = new Ellipse2D.Float(position.getX(), position.getY(),radius*2, radius*2);
			Color c = null;
			if(agentsRef.get(i).color.equals(Simulator.socialColorString))
				c = Simulator.socialColor;
			else if(agentsRef.get(i).color.equals(Simulator.qlearningColorString))
				c =  Simulator.qlearningColor;
			else if (agentsRef.get(i).color.equals(Simulator.qinterpreterColorString))
				c = Simulator.qinterpreterColor;
			else if (agentsRef.get(i).color.equals(Simulator.qinterpreterLoggedColorString))
				c = Simulator.qinterpreterLoggedColor;
			else
				c = Simulator.defaultColor;
			agentShapes.add(new ReplayShape(ell, c));
		}
	
		renderReplayFrame = true;
		Simulator.getGuiFrame().UpdateSimPanel();
		
		if (framesPassed++ == FRAMESTAY || frameJumping) {
			framesPassed = 0;
			currentStep = step;
		}
		
		Simulator.guiFrame.updateSliderWithValue(getPercProgress());
		renderReplayFrame = false;

		frameJumping = false;
		
		return limitFrame;
	}





	public List<ReplayShape> getAgentShapes() {
		
		return agentShapes;
	}



	public EnvironmentManager getEnvManager() {
		return envManager;
	}



	public Map<Integer, AgentSoftReference> getAgentsRef() {
		return agentsRef;
	}
	

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}


	
	
}

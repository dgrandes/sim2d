package com.pf.simulator;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.pf.gui.GUIFrame;
import com.pf.gui.GuiStates;
import com.pf.gui.SwingWorker;
import com.pf.logging.IBaseLogger;
import com.pf.logging.ISimulatorLogger;
import com.pf.logging.QLearningLogger;
import com.pf.logging.SimulatorLoggerASCII;
import com.pf.logging.SimulatorLoggerBinary;
import com.pf.model.Agent;
import com.pf.movement.qlearning.QLearningBaseMovement;
import com.pf.movement.qlearning.QLearningMovement;
import com.pf.movement.qlearning.interpreter.QInterpreterMovement;
import com.pf.simulator.log.StateLog;
import com.pf.simulator.replay.Replay;

public class Simulator {

	public static SimEnvironment simEnvironment;
	public static Simulation simulation;
	public static GUIFrame guiFrame;
	public static Replay replay;
	private static ArrayList<IBaseLogger> loggers = new ArrayList<IBaseLogger>();
	public static ISimulatorLogger simlogger;
	public static SwingWorker worker;
	public static String socialColorString = "social";
	public static Color socialColor = new Color(0x1f14ff);
	public static String qlearningColorString = "qlearning";
	public static Color qlearningColor = new Color(0xff1414);
	public static String qinterpreterColorString = "qinterpreter";
	public static Color qinterpreterColor = new Color(0x603295);
	public static String qinterpreterLoggedColorString = "qinterpreterlogged";
	public static Color qinterpreterLoggedColor = new Color(0x03c500);
	public static String defaultColorString = "default";
	public static Color defaultColor = new Color(0xb57b00);
;	

	public static void main(String[] args) {
		simEnvironment = new SimEnvironment();
		simulation = new Simulation();
		CreateGUIFrame();

	}

	public static void CreateGUIFrame() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				guiFrame = new GUIFrame();

			}

		});
	}

	public static SimEnvironment getSimEnvironment() {
		if(simEnvironment == null)
			simEnvironment = new SimEnvironment();
		return simEnvironment;
	}

	public static Simulation getSimulation() {
		if(simulation == null)
			simulation = new Simulation();
		return simulation;
	}

	public static GUIFrame getGuiFrame() {
		return guiFrame;
	}

	public static Replay getReplay() throws Exception {
		if (replay == null)
			throw new Exception("replay is null");
		return replay;
	}

	public static void setReplay(Replay replay) {

		Simulator.replay = replay;
	}

	public static void loadEnvironment(String path) throws Exception {
		
		simEnvironment.resetEnvironment();
		simEnvironment.configureEnvironmentFromFile(path);
		guiFrame.environmentLoaded();
		simulation.envFilePath = path;
		replay = null;
	}

	public static void unloadSimulation(){
		QInterpreterMovement.reset();
	}
	
	public static void reloadSimulation() throws Exception {
		
		resetLogs();
		loadEnvironment(simulation.envFilePath);
		loadAgents(simulation.agentsFilePath);
		
		

	}
	


	public static void openLoggers() throws Exception
	{
		if(guiFrame.menu.binaryFormatItem.getState())
			simlogger = new SimulatorLoggerBinary();
		else
			simlogger = new SimulatorLoggerASCII();
		simlogger.openLog();
		for(IBaseLogger l : loggers)
		{
			l.openLog();
		}

	}

	
	public static void addLog(IBaseLogger log)
	{
		loggers.add(log);
	}
	
	public static void logState() throws Exception
	{
		if(simlogger != null)
			simlogger.writeState();
	}
	
	public static void resetLogs() throws Exception
	{
		if(simlogger != null)
			simlogger.closeLog();
		loggers.clear();
		simlogger = null;
	}
	public static void closeLogs() throws Exception
	{
		if(loggers != null)
			for(IBaseLogger l : loggers)
			{
				l.closeLog();
			}
		else
			loggers = new ArrayList<IBaseLogger>();
		resetLogs();

	}
	
	public static QLearningBaseMovement getMvtFromAgent(int Id) throws Exception
	{
		QLearningLogger logger;
		if(loggers != null)
		{
			for(IBaseLogger l : loggers)
			{
				if(l instanceof QLearningLogger)
				{
					logger = (QLearningLogger) l;
					if(logger.getAgent().id == Id)
					{
						return logger.getMvt();
					}
				}
			}
		}
		throw new Exception("No such QLearning Agent");
	}
	
	public static int getFirstQLearningAgentId() throws Exception
	{
		List<Agent> agents= null;
		try{
			agents = simEnvironment.getAgentManager().getAgents();
		}catch(Exception e)
		{
			throw new Exception("No Simulation has been loaded!");
		}
		for(Agent a: agents)
		{
			if (a.logger instanceof QLearningLogger)
				return a.id;
		}
		throw new Exception("No QLearning Agents!");
	
	}
	public static void loadAgents(String path) throws Exception {
		simEnvironment.configureAgentsFromFile(path);
		guiFrame.agentsLoaded();
		simulation.agentsFilePath = path;
	}
	
	public static void registerNewAgent(Agent a, float time)
	{
		if(simlogger != null)
			simlogger.putNewAgent(a, time);
	}
	
	public static void registerDeletedAgent(Agent a, float time)
	{
		if(simlogger != null)
		{	
			simlogger.agentDied(a, time);
			if (a.movement instanceof QLearningMovement)
			{
				QLearningLogger l = ((QLearningLogger) a.logger);
				l.logVictory();
			}
		}	
		
	}

	private static void runSimulation() {
		if (worker == null || worker.get() != null) {
			worker = new SwingWorker() {

				@Override
				public Object construct() {
					try {
						return doWork();
						
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}

				public void finished() {
					System.err.println("run sim: Worker Finished");
					stopCallback();
				}
			};
			worker.start();

		} else {
			System.err.println("RunSimulation: worker executing already!...");

			worker.interrupt();
			worker = null;
		}
	}

	public static void playSimulation() {
		System.err.println("Play");
		guiFrame.state.changeState(GuiStates.playing);
		runSimulation();
		guiFrame.UpdateSimPanel();

	}

	public static void pauseSimulation() {
		System.err.println("Pausing");
		guiFrame.state.changeState(GuiStates.paused);
	}

	public static void stopSimulation() {
		guiFrame.state.changeState(GuiStates.stopped);
		if(guiFrame.state.getPreviousState() == GuiStates.paused)
			stopCallback();
	}

	public static void stopRecording() {
		stopSimulation();
	}

	public static void recSimulation() {
		//StateLog.get().setActive(true);
		
		try {
			openLoggers();
			guiFrame.state.changeState(GuiStates.recording);
			runSimulation();
		} catch (Exception e) {
			System.err.println("Can't start recording, couldnt open the loggers!");
		}
	

	}
/*
	public static void saveSimulationLog(String path) {
		StateLog.get().SaveLog(path);

	}*/

	public static void stopCallback() {
		
		System.err.println("Stop callback, worker said: "+worker.get());
		GuiStates state = guiFrame.state.getCurrentState();
		
		if(guiFrame.state.getPreviousState() == GuiStates.recording)
		{
			try {
				closeLogs();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Closing the logs failed!!!");
			}
		}
		if(state == GuiStates.paused)
			return;
		
		
		
		if (state == GuiStates.shuttingdown) {
			simulation.close();
			System.err.println("Simulator goes offline!");
			return;
		}

		simulation.reset();
		
		try {
			reloadSimulation();
			guiFrame.state.changeState(GuiStates.loaded);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}
	
	public static void loadReplay()
	{
		replay = new Replay();
		
		guiFrame.enableReplay();
		runReplay();
		
	}
	
	public static void loadReplayReference(String path) throws Exception
	{
		StateLog log = StateLog.get();
		log.reset();
		log.loadReferenceFile(path);
		
	}

	public static void loadReplaySteps(String path) throws Exception
	{
		StateLog log = StateLog.get();
		log.loadStepsFile(path);
	}
	
	
	public static void playReplay()
	{
		System.err.println("Replay Playing");
		guiFrame.state.changeState(GuiStates.replay_playing);
		runReplay();
		guiFrame.UpdateSimPanel();
	}
	
	
	public static void runReplay()
	{
		if (worker == null || worker.get() != null) {
			worker = new SwingWorker() {

				@Override
				public Object construct() {
					try {
						return doReplayWork();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}

				public void finished() {
					
				}
			};
			worker.start();

		} else {
			System.err.println("RunReplay: worker executing already!...");

			worker.interrupt();
			worker = null;
		}
	}
	
	public static boolean renderReplayFrame(int frame)
	{
		return replay.replayStep(frame);
	}
	
	public static void rewReplay()
	{
		System.err.println("rew replay");
		guiFrame.state.changeState(GuiStates.replay_rew);
		runReplay();
	}
	
	public static void fwdReplay()
	{
		System.err.println("fwd replay");
		guiFrame.state.changeState(GuiStates.replay_fwd);
		runReplay();
	}
	
	public static void pauseReplay()
	{
		System.err.println("pausing replay");
		guiFrame.state.changeState(GuiStates.replay_paused);
	}
	


	public static void shutdown() {
		System.out.println("Shutting down");
		guiFrame.state.changeState(GuiStates.shuttingdown);


	}

	public static void restartReplay()
	{
		System.err.println("Restarting replay");
		guiFrame.state.changeState(GuiStates.replay_start);
		runReplay();
	}
	
	public static void endReplay()
	{
		System.err.println("ending replay");
		guiFrame.state.changeState(GuiStates.replay_end);
		runReplay();
	}
	
	public static void replayRenderDone()
	{
		if(guiFrame.state.getCurrentState() == GuiStates.replay_playing)
			runReplay();
		else if(guiFrame.state.getCurrentState() == GuiStates.replay_start)
			guiFrame.state.changeState(GuiStates.replay_loaded);

	}
	
	public static void stopReplay()
	{
		System.err.println("Stopping replay");
		guiFrame.state.changeState(GuiStates.replay_stop);
		runReplay();
		
	}
	
	public static Object doReplayWork() throws Exception{

			GuiStates state = Simulator.getGuiFrame().state.getCurrentState();
			boolean borderframe;
			if(state == GuiStates.replay_loaded)
				renderReplayFrame(0);
			if(state == GuiStates.replay_playing)
			{
				borderframe = renderReplayFrame(1);
				if(borderframe)
					guiFrame.state.changeState(GuiStates.replay_end);
				return "play frame done";
			}	
			else if (state == GuiStates.replay_paused)
				return "replay paused";
			else if (state == GuiStates.replay_rew)
			{
				borderframe = renderReplayFrame(-1);
				if(borderframe)
					guiFrame.state.changeState(GuiStates.replay_loaded);
				return "replay paused at rew";
			}
			else if(state == GuiStates.replay_fwd)
			{
				borderframe = renderReplayFrame(1);
				if(borderframe)
					guiFrame.state.changeState(GuiStates.replay_end);
				return "replay paused at fwd";
			}
			else if(state == GuiStates.replay_stop)
			{
				//borderframe = renderReplayFrame(Integer.MIN_VALUE);
				return "replay stopped";
			}
			else if(state == GuiStates.replay_start)
			{
				renderReplayFrame(Integer.MIN_VALUE);
				return "replay restarted";
			}
			else if (state == GuiStates.replay_end)
			{
				renderReplayFrame(Integer.MAX_VALUE);
				return "replay ended";
			}
			return "nothing";

	}
	
	
	public static Object doWork() throws Exception {
		System.err.println("Worker Thread Started!");

		while (true) {

			GuiStates state = Simulator.getGuiFrame().state.getCurrentState();
			if (state == GuiStates.playing || state == GuiStates.recording) {
				float steps = Simulator.getSimEnvironment().getSteps();
				float cur_steps = Simulator.getSimulation().getCurrentStep();
				if (!Simulator.getSimEnvironment().hasEnd()
						|| cur_steps < steps)

					Simulator.getSimulation().simulateStep();

			} else if (state == GuiStates.paused) {
				return "Terminating worker, pause pressed";
			} else if (state == GuiStates.stopped )
			{
				return "Terminating worker, stop pressed";
			}
			else if(state == GuiStates.shuttingdown) {
				return "Terminating worker, simulator shutting down";
			}
		}

	}

	public static void configureAgent(Agent a, String movementType) throws Exception {
		getSimEnvironment().configureAgentsMovement(a,movementType);
		
	}

	public static void configureAgents(List<Agent> newAgents) throws Exception {
		simEnvironment.configureAgents(newAgents);
		for(Agent a: newAgents)
		{
			Simulator.registerNewAgent(a, simulation.getCurrentStep() * simEnvironment.getEnvironment().deltaTime);
		}
		
	}

}

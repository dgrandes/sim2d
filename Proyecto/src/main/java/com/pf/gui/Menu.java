package com.pf.gui;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public JMenuItem saveItem;
	JMenuItem frameskip;
	JMenuItem recframeskip;
	JMenuItem replayfps;
	JMenuItem loadReplay;
	JMenuItem qLOptions;
	public JCheckBoxMenuItem binaryFormatItem;
	public JCheckBoxMenuItem compressData;
	

	public Menu() {

		add(createFileMenu());
		// add(createPrimitivesMenu());
		add(createSimOptionsMenu());
		add(createQLearningMenu());

	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");

		JMenuItem loadItem = new JMenuItem("Load Simulation", new ImageIcon(
				ClassLoader.getSystemResource("sim.png")));

		loadReplay = new JMenuItem("Load Replay", new ImageIcon(
				ClassLoader.getSystemResource("replay.png")));

		JMenuItem saveAItem = new JMenuItem("Save Agents", new ImageIcon(
				ClassLoader.getSystemResource("robot.png")));

		JMenuItem loadAItem = new JMenuItem("Load Agents", new ImageIcon(
				ClassLoader.getSystemResource("robot.png")));

		JMenuItem saveEItem = new JMenuItem("Save Environment", new ImageIcon(
				ClassLoader.getSystemResource("world.png")));
		JMenuItem loadEItem = new JMenuItem("Load Environment", new ImageIcon(
				ClassLoader.getSystemResource("world.png")));

		loadReplay
				.addActionListener(new MenuActionListener.LoadReplayActionListener());
		saveAItem
				.addActionListener(new MenuActionListener.SaveAgentsActionListener());
		loadAItem
				.addActionListener(new MenuActionListener.LoadAgentsActionListener());
		loadItem.addActionListener(new MenuActionListener.LoadSimulationActionListener());
		saveEItem
				.addActionListener(new MenuActionListener.SaveEnvironmentActionListener());
		loadEItem
				.addActionListener(new MenuActionListener.LoadEnvironmentActionListener());

		fileMenu.add(loadItem);
		fileMenu.add(loadReplay);
		fileMenu.addSeparator();
		fileMenu.add(saveAItem);
		fileMenu.add(loadAItem);
		fileMenu.addSeparator();
		fileMenu.add(saveEItem);
		fileMenu.add(loadEItem);

		return fileMenu;
	}

	private JMenu createQLearningMenu() {
		JMenu fileMenu = new JMenu("QLearning");

	/*	saveItem = new JMenuItem("Save Simulation", new ImageIcon(
				"src/images/sim.png"));*/
		
		qLOptions = new JMenuItem("Q Learning Options");
		
		qLOptions
				.addActionListener(new MenuActionListener.ShowQLOptions());
	
		fileMenu.add(qLOptions);
		return fileMenu;
	}

	@SuppressWarnings("unused")
	private JMenu createPrimitivesMenu() {

		JMenu menu = new JMenu("Primitives");

		JMenuItem cagent = new JMenuItem("Create Agent", new ImageIcon(
				ClassLoader.getSystemResource("robot.png")));

		JMenuItem cspawn = new JMenuItem("Create Spawn", new ImageIcon(
				ClassLoader.getSystemResource("spawn.png")));

		JMenuItem cexit = new JMenuItem("Create Exit", new ImageIcon(
				ClassLoader.getSystemResource("exit.png")));

		JMenuItem box = new JMenuItem("Create Square Obstacle", new ImageIcon(
				ClassLoader.getSystemResource("box.png")));

		JMenuItem circle = new JMenuItem("Create Circle Obstacle",
				new ImageIcon(ClassLoader.getSystemResource("circle.png")));

		menu.add(cspawn);
		menu.add(cexit);
		menu.addSeparator();
		menu.add(box);
		menu.add(circle);
		menu.addSeparator();
		menu.add(cagent);
		return menu;

	}

	private JMenu createSimOptionsMenu() {
		JMenu simOptions;
		simOptions = new JMenu("Simulation Options");

		frameskip = new JMenuItem("Sim Frameskip");
		frameskip
				.addActionListener(new MenuActionListener.SetFrameSkipActionListener());

		recframeskip = new JMenuItem("Rec Frameskip");
		recframeskip
				.addActionListener(new MenuActionListener.SetRecFrameSkipActionListener());

		replayfps = new JMenuItem("Replay FPS");
		replayfps
				.addActionListener(new MenuActionListener.SetReplayFPSListener());

		simOptions.add(frameskip);
		frameskip.setEnabled(false);
		simOptions.add(recframeskip);
		recframeskip.setEnabled(false);
		replayfps.setEnabled(false);
		simOptions.add(replayfps);
	    binaryFormatItem = new JCheckBoxMenuItem("Save Replay in Binary Format");
	    simOptions.add(binaryFormatItem);
	    compressData = new JCheckBoxMenuItem("Compress Binary Data");
	    simOptions.add(compressData);
		return simOptions;
	}

	public void EnableSimOptions() {
		frameskip.setEnabled(true);
		recframeskip.setEnabled(true);
		replayfps.setEnabled(false);
	}

	public void EnableReplayOptions() {
		frameskip.setEnabled(false);
		recframeskip.setEnabled(false);
		replayfps.setEnabled(true);
	}
	
	public void EnableQLearningOptions(){
		qLOptions.setEnabled(true);
	}
}
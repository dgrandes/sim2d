package com.pf.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.pf.model.Agent;
import com.pf.simulator.SimEnvironment;
import com.pf.simulator.Simulator;

public class GUIFrame extends JFrame implements InternalFrameListener,
		WindowListener {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 760;
	private static final int simPanelWidth = 900;
	private static final int simPanelHeight = 480;
	private static final int debugWidth = 380;
	private static final int debugHeight = 480;
	private static final int debugX = 900;
	private static final int debugY = 280;

	//Directory that was last chosen fromt the file chooser
	public  String baseDirectory;
	public  String fileStem;
	public ButtonPanel buttonPanel;
	public SimEnvironment env;
	public SimPanel simPanel;
	public Menu menu;
	public ProgressBarFrame prgbarframe;
	public ReplaySlider replaySlider;
	private JDesktopPane debugPane;
	public GuiObservableState state;
	private float oldervalue = 0;
	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	private ArrayList<DebugFrame> dframes = new ArrayList<DebugFrame>();

	public GUIFrame() {
		setTitle("Simulator");
		env = Simulator.getSimEnvironment();

		simPanel = new SimPanel();
		simPanel.addMouseListener(simPanel);
		simPanel.addMouseWheelListener(simPanel);
		simPanel.addMouseMotionListener(simPanel);

		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		debugPane = new JDesktopPane();

		debugPane.setBounds(debugX, debugY, debugWidth, debugHeight);

		debugPane.setBackground(Color.black);
		add(debugPane);

		state = new GuiObservableState();

		buttonPanel = new ButtonPanel(state);

		state.changeState(GuiStates.preloaded);

		// if (!Simulator.getSimEnvironment().isEnvironmentReady())
		// buttonPanel.disableAllButtons();

		add(buttonPanel, BorderLayout.NORTH);

		menu = new Menu();
		setJMenuBar(menu);

		simPanel.setPreferredSize(new Dimension(simPanelWidth, simPanelHeight));

		Border simBorder = BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED);

		simPanel.setBorder(simBorder);

		add(simPanel, BorderLayout.WEST);
		UpdateSimPanel();
		pack();
		addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);

	}

	
	public void removeProgressBar()
	{
		if(prgbarframe != null)
		{
			debugPane.remove(prgbarframe);
			oldervalue = 0;
			prgbarframe = null;
			debugPane.validate();
			debugPane.repaint();
		}
	}
	
	public void removeSlider()
	{
		if(replaySlider != null)
		{
			debugPane.remove(replaySlider);
			replaySlider = null;
			debugPane.validate();
			debugPane.repaint();
		}
	}

	public boolean isPanelDrawing() {
		return simPanel.drawingAgents;
	}

	public void UpdateSimPanel() {

		// Still hasnt finished previous render

		if (simPanel.drawingAgents) {
			System.out.println("Drawing");
			return;
		}

		Runnable updateSimPanel = new Runnable() {
			public void run() {
				simPanel.repaint();
			}
		};

		SwingUtilities.invokeLater(updateSimPanel);
		UpdateDebugFrames();
	}

	public void enableReplay() {
		menu.EnableReplayOptions();
		state.changeState(GuiStates.replay_loaded);

		UpdateSimPanel();

	}

	public void updateSliderWithValue(int value)
	{
		if(replaySlider == null)
		{
			 replaySlider = new ReplaySlider();

			debugPane.add(replaySlider);
			// prgbarframe.setPreferredSize(new Dimension(380,100));
			replaySlider.setVisible(true);
			replaySlider.pack();
		}
		replaySlider.setValue(value);


	}
	public void showError(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showConfirmation(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void environmentLoaded() {
		// buttonPanel.reset();
		reset();
		
		if (Simulator.getSimEnvironment().isEnvironmentReady())
			state.changeState(GuiStates.loaded);

		menu.EnableSimOptions();
		UpdateSimPanel();
	}
	
	public void reset()
	{
		removeProgressBar();
		removeSlider();
		simPanel.reset();
	}

	public void agentsLoaded() {
		if (!Simulator.getSimEnvironment().isEnvironmentReady())
			return;
		state.changeState(GuiStates.loaded);

		menu.EnableSimOptions();
		UpdateSimPanel();
		Simulator.getSimulation().createEnvThreads();

	}

	public void createDebugFrame(Agent a) {

		DebugFrame d = new DebugFrame(a, env);

		debugPane.add(d);
		d.setVisible(true);

		d.pack();
		try {
			d.setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dframes.add(d);

	}

	public void updateProgressBar(float value) {
		if(value != oldervalue)
		{
			System.out.println(value);
			oldervalue = value;
		}
		
		if (prgbarframe == null) {
			prgbarframe = new ProgressBarFrame();

			debugPane.add(prgbarframe);
			// prgbarframe.setPreferredSize(new Dimension(380,100));
			prgbarframe.setVisible(true);
			prgbarframe.pack();
		}
		prgbarframe.updateProgress(value);
	}

	public String promptUserForFileName() {

		final JFileChooser fc = MenuActionListener.fc;

		int returnVal = fc.showDialog(null, "Enter File");

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			baseDirectory =  file.getParent();
			fileStem = file.getName();
			System.out.println(baseDirectory);
			System.out.println(fileStem);
			System.out.println(file.getAbsolutePath());
			return file.getAbsolutePath();

		}

		return null;
	}

	public void UpdateDebugFrames() {
		Runnable UpdateDebugPanelRunnable = new Runnable() {
			public void run() {
				for (DebugFrame d : dframes)
					d.Update();
			}
		};
		SwingUtilities.invokeLater(UpdateDebugPanelRunnable);

	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		DebugFrame d = (DebugFrame) e.getInternalFrame();
		dframes.remove(d);
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// buttonPanel.mybuttons.get("stop").button.doClick();
		// Simulator.getSimulation().close();
		Simulator.shutdown();
		dispose();
		System.out.println("Simulator is exiting!");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				System.exit(0);
			}

		});

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
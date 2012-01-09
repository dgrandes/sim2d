package com.pf.gui;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public abstract class GuiButton implements Observer {


	public JButton button;
	public String key;
	private HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	private HashMap<String, ActionListener> listeners = new HashMap<String, ActionListener>();
	public int index;
	
	public GuiButton(String key, int index)
	{
		this.key = key;
		button = new JButton();
		this.index = index;
	}	
	
	
	public void addIcon(String key, ImageIcon icon)
	{
		icons.put(key, icon);
	}
	
	public void setIcon(String key)
	{
		button.setIcon(icons.get(key));
	}
	

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof GuiStates)
		{
			switch((GuiStates)arg)
			{
			case preloaded:
				onPreload();break;
			case loaded:
				onLoad();break;
			case playing:
				onPlaying();break;
			case stopped:
				onStop();break;
			case paused:
				onPause();break;
			case recording:
				onRec();break;
			case replay_loaded:
				onReplayLoaded();break;
			case replay_playing:
				onReplayPlaying(); break;
			case replay_paused:
				onReplayPause(); break;
			case replay_stop:
				onReplayStop(); break;
			case replay_start:
				onReplayStart(); break;
			case replay_end:
				onReplayEnd(); break;
			case replay_fwd:
				onReplayFwd(); break;
			case replay_rew:
				onReplayRew(); break;
			}
				
		}

	}
	
	public void setListener( String key)
	{
		
		removeAllListeners();
		button.addActionListener(listeners.get(key));
		
	}

	public void addListener(String key, ActionListener l)
	{
		listeners.put(key, l);
	}
	public void removeAllListeners()
	{
		ActionListener[] listeners = button.getActionListeners();
		for(ActionListener li : listeners)
		{
			button.removeActionListener(li);
		}
	}
	
	public void onPreload()
	{
		button.setEnabled(false);
		removeAllListeners();
		
	}
	
	
	public void onLoad(){}
	public void onPlaying(){}
	public void onPause(){}
	public void onStop(){}
	public void onRec(){}
	public void onReplayLoaded(){}
	public void onReplayPlaying(){}
	public void onReplayRew(){}
	public void onReplayFwd(){}
	public void onReplayStart(){}
	public void onReplayEnd(){}
	public void onReplayStop(){}
	public void onReplayPause(){}
	
	
}

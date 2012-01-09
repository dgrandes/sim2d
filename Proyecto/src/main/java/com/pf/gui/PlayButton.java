package com.pf.gui;


public class PlayButton extends GuiButton {

	public PlayButton(String key, int index) {
		super(key, index);
	}

	@Override
	public void onLoad() {
		button.setEnabled(true);
		setListener("play");
		setIcon("play");
	

	}

	@Override
	public void onPlaying() {
		setIcon("pause");
		setListener("pause");
		
	}

	@Override
	public void onPause() {
		setIcon("play");
		setListener("play");
		
		
	}

	@Override
	public void onStop() {
		setIcon("play");
		setListener("play");
		
	}

	@Override
	public void onRec()
	{
		button.setEnabled(false);
		
	}
	@Override
	public void onReplayLoaded()
	{
		button.setEnabled(true);
		setIcon("play");
		setListener("replay");
	}
	
	@Override
	public void onReplayPlaying()
	{
		setIcon("pause");
		setListener("pauseReplay");
	}
	
	@Override 
	public void onReplayPause()
	{
		setIcon("play");
		setListener("replay");
		
	}
	
	@Override
	public void onReplayStop()
	{
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayEnd()
	{
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayFwd()
	{
		setIcon("play");
		setListener("replay");
	}
	
	@Override
	public void onReplayRew()
	{
		if(!button.isEnabled())
			button.setEnabled(true);
		setIcon("play");
		setListener("replay");
	}
}

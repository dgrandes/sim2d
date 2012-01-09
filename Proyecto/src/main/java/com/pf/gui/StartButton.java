package com.pf.gui;


public class StartButton extends GuiButton {

	public StartButton(String key, int index) {
		super(key, index);
	}

	@Override
	public void onReplayPlaying() {
		button.setEnabled(true);
		
	}
	
	@Override
	public void onReplayLoaded()
	{
		button.setEnabled(false);
		setListener("start");
	}
	
	public void onReplayEnd()
	{
		button.setEnabled(true);
		
	}
	
	@Override
	public void onLoad()
	{
		button.setEnabled(false);
	}


}

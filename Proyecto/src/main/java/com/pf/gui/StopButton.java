package com.pf.gui;


public class StopButton extends GuiButton {

	public StopButton(String key, int index) {
		super(key, index);
	}

	@Override
	public void onLoad() {
		

	}

	@Override
	public void onPlaying() {
		button.setEnabled(true);
		setListener("stop");
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRec()
	{
		button.setEnabled(true);
		setListener("stop");
	}
	@Override
	public void onStop() {
		button.setEnabled(false);
		
	}
	
	@Override
	public void onReplayLoaded(){
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayPlaying()
	{
		button.setEnabled(true);
		setListener("replay");
	}
	
	public void onReplayStop()
	{
		button.setEnabled(false);
	}
	
	public void onReplayEnd()
	{
		button.setEnabled(false);
	}

}

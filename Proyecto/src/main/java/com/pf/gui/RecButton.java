package com.pf.gui;


public class RecButton extends GuiButton {

	public RecButton(String key, int index) {
		super(key, index);
	}


	@Override
	public void onLoad() {
		button.setEnabled(true);
		setListener("rec");

	}


	@Override
	public void onPlaying() {
		button.setEnabled(false);
		
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStop() {
		
		
	}

	public void onRec(){
		button.setEnabled(false);
	}
	
	@Override
	public void onReplayLoaded()
	{
		button.setEnabled(false);
	}
}

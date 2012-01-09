package com.pf.gui;

import java.util.Observable;

public class GuiObservableState extends Observable {

	private GuiStates state;
	private GuiStates previousstate;
	
	public GuiObservableState()
	{
		state = GuiStates.preloaded;
		previousstate = state;
	}
	
	public void changeState(GuiStates newstate)
	{
		previousstate = state;
		state = newstate;
		setChanged();
		notifyObservers(state);
	}
	
	public GuiStates getCurrentState()
	{
		return state;
	}
	
	public GuiStates getPreviousState()
	{
		return previousstate;
	}
	
}

package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface ControllerConnections extends PropertyChangeListener{
	
	public void propertyChange(PropertyChangeEvent evt);
}

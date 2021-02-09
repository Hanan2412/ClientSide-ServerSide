package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface ServerContoller extends PropertyChangeListener{

	public void propertyChange(PropertyChangeEvent evt);
	
	
}

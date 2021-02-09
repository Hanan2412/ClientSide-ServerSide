package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface ServerDataBase extends PropertyChangeListener{

	public void propertyChange(PropertyChangeEvent evt);
}

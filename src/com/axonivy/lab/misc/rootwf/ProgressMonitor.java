package com.axonivy.lab.misc.rootwf;

public interface ProgressMonitor {
	
	public void update(String progress, Object... formatingValues);
	
}

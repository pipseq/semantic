package org.pipseq.spin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.progress.ProgressMonitor;

public class SpinMonitor implements ProgressMonitor {
	private static final Logger log = LoggerFactory.getLogger(SpinMonitor.class);
	private boolean cancelled = false;

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return cancelled;
	}

	@Override
	public void beginTask(String label, int totalWork) {
		log.debug("label="+label+"; totalWork="+totalWork);
		
	}

	@Override
	public void done() {
		log.debug("done");
		
	}

	@Override
	public void setCanceled(boolean value) {
		cancelled = value;
		
	}

	@Override
	public void setTaskName(String value) {
		log.debug("taskName="+value);
		
	}

	@Override
	public void subTask(String label) {
		log.debug("subTask="+label);
		
	}

	@Override
	public void worked(int amount) {
		log.debug("worked="+amount);

		
	}

}

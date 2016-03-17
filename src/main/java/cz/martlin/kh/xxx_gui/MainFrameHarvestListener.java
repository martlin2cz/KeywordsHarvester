package cz.martlin.kh.xxx_gui;

import cz.martlin.kh.logic.harvest3.HarvestingListener;

/**
 * Implements {@link HarvestingListener} such that each message is shown in
 * {@link JMainFrame} in status label.
 * 
 * @author martin
 * 
 */
public class MainFrameHarvestListener implements HarvestingListener {

	private final JMainFrame frame;

	public MainFrameHarvestListener(JMainFrame frame) {
		this.frame = frame;
	}

	@Override
	public void occured(String what) {
		frame.setStatus(what);
	}

}

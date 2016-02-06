package cz.martlin.kh.gui;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.utils.Interruptable;

public class FormDataUpdateThread extends Thread implements Interruptable {

	private final JMainFrame frame;
	private final Config config;

	private boolean interrupted;

	public FormDataUpdateThread(JMainFrame frame, Config config) {
		super("FormDataUpdT");
		this.frame = frame;
		this.config = config;
	}

	@Override
	public void interrupt() {
		super.interrupt();
		interrupted = true;
	}

	@Override
	public synchronized void start() {
		while (!interrupted) {
			frame.updateDataInFrame();
			try {
				Thread.sleep(config.getFormUpdateInterval());
			} catch (InterruptedException e) {
			}
		}
	}

}

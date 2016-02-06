package cz.martlin.kh.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import layout.SpringUtilities;
import cz.martlin.kh.KHMain;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.export.AbstractExporter;
import cz.martlin.kh.logic.harvest.HarvestProcessData;
import cz.martlin.kh.logic.harvest.ParalellHarvester;

/**
 * Main frame of application.
 * 
 * @author martin
 * 
 */
public class JMainFrame extends JFrame {

	private static final long serialVersionUID = 6209520369264005128L;
	private static final String TITLE = KHMain.APP_NAME;

	public static final Dimension LEFT_LABEL_PREF_SIZE = new Dimension(20, 20);

	private final Config config;

	private FormDataUpdateThread updateThread;
	private ParalellHarvester harvester;
	private HarvestProcessData data;

	private final JEditKeywordsDial editDial;

	private JButton startButt;
	private JButton stopButt;
	private JButton loadPrevButt;
	private JButton importButt;
	private JLabel outputFileLbl;
	private JButton changeFileButt;
	private JLabel waitingLbl;
	private JButton editQueueButt;
	private JLabel doneLbl;
	private JButton viewDoneButt;

	public JMainFrame(Config config) {
		super(TITLE);
		this.config = config;
		this.editDial = new JEditKeywordsDial(this, config);

		initializeComponents();

		updateDataInFrame();

		Dimension size = new Dimension(500, 300);
		setPreferredSize(size);
		setMinimumSize(size);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	private void initializeComponents() {
		JPanel content = new JPanel();
		initMainPane(content);
		getContentPane().add(content, BorderLayout.CENTER);

		JLabel footer = new JLabel("<html><i>" + KHMain.getAbout()
				+ "</i></html>");
		getContentPane().add(footer, BorderLayout.SOUTH);
	}

	private void initMainPane(JPanel pane) {
		pane.setLayout(new SpringLayout());

		// start and stop
		startButt = new JButton("<html><big>Start harvesting!</big></html>");
		startButt.addActionListener(new StartButtActionListener());
		pane.add(startButt);

		stopButt = new JButton("Stop");
		stopButt.addActionListener(new StopButtActionListener());
		pane.add(stopButt);

		// load previous and import
		loadPrevButt = new JButton("Open previous harvest");
		loadPrevButt.addActionListener(new LoadPreviousButtActionListener());
		pane.add(loadPrevButt);

		importButt = new JButton("Import keywords");
		importButt.addActionListener(new ImportKeywordsButtActionListener());
		pane.add(importButt);

		// waiting and keywords edit
		waitingLbl = new JLabel("Waiting to be prossed: --");
		waitingLbl.setPreferredSize(LEFT_LABEL_PREF_SIZE);
		pane.add(waitingLbl);

		editQueueButt = new JButton("Edit keywords");
		editQueueButt.addActionListener(new EditQueueButtActionListener());
		pane.add(editQueueButt);

		// done and view output
		doneLbl = new JLabel("Done: --");
		doneLbl.setPreferredSize(LEFT_LABEL_PREF_SIZE);
		pane.add(doneLbl);

		viewDoneButt = new JButton("View result");
		viewDoneButt.addActionListener(new ViewDoneButtActionListener());
		pane.add(viewDoneButt);

		// output
		outputFileLbl = new JLabel("Output file: --"
				+ config.getExportFile().getPath());
		outputFileLbl.setPreferredSize(LEFT_LABEL_PREF_SIZE);
		outputFileLbl.setToolTipText(config.getExportFile().getPath());
		pane.add(outputFileLbl);

		changeFileButt = new JButton("Change file");
		changeFileButt.addActionListener(new ChangeFileButtActionListner());
		pane.add(changeFileButt);

		SpringUtilities.makeCompactGrid(pane, 5, 2, 10, 10, 10, 15);

	}

	private boolean isRunning() {
		return harvester != null;
	}

	/**
	 * Updates data shown in frame by {@link #data} and {@link #config}.
	 */
	public void updateDataInFrame() {
		stopButt.setText("Stop");

		String path = config.getExportFile().getPath();
		outputFileLbl.setText("Output file: " + path);

		int waiting = (data == null) ? 0 : data.getTosubkeywordCount();
		waitingLbl.setText("Waiting to be processed: " + waiting);

		int done = (data == null) ? 0 : data.getExportedCount();
		doneLbl.setText("Done: " + done);

		setEnabledButts();
	}

	private void setEnabledButts() {
		boolean isRunning = isRunning();

		startButt.setEnabled(!isRunning);
		stopButt.setEnabled(isRunning);

		loadPrevButt.setEnabled(!isRunning);
		importButt.setEnabled(!isRunning);
		changeFileButt.setEnabled(!isRunning);
		editQueueButt.setEnabled(!isRunning);
		viewDoneButt.setEnabled(!isRunning);
	}

	/**
	 * Starts harvesting.
	 */
	public void start() {
		startButt.setEnabled(false);
		new StartThread(this).start();
	}

	/**
	 * Stops harvesting and awaits stop.
	 */
	public void stop() {
		stopButt.setEnabled(false);
		new StopThread(this).start();
	}

	/**
	 * Shows {@link #editDial}.
	 */
	public void editKeywords() {
		if (data == null) {
			data = HarvestProcessData.createNew(config,
					Collections.<String> emptySet());
		}

		editDial.setToData(data);
		editDial.setVisible(true);
	}

	/**
	 * Tries to load previously saved harvest (by
	 * {@link Config#getQueuesDumpFile()}).
	 */
	public void loadPreviousHarvest() {
		HarvestProcessData newData = HarvestProcessData
				.loadFromDumpFile(config);
		if (newData == null) {
			error("Could not load previous harvest. Check file "
					+ config.getQueuesDumpFile().getPath());
			return;
		}

		data = newData;
		updateDataInFrame();
	}

	/**
	 * Shows open file dialog for import keywords.
	 */
	public void importKeywords() {
		JFileChooser chooser = createImportFileChooser();
		int result = chooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {

			File file = chooser.getSelectedFile();
			TxtWithSeparatorFileFilter filter = (TxtWithSeparatorFileFilter) chooser
					.getFileFilter();
			HarvestProcessData newData = HarvestProcessData.importThem(config,
					file, filter.getSeparator());
			if (newData != null) {
				data = newData;
			} else {
				error("Could not import keywords from file");
			}
		}

		updateDataInFrame();
	}

	/**
	 * Opens file chooser to change export ({@link Config#getExportFile()}).
	 */
	public void chooseExportFile() {
		File file = config.getExportFile();

		JExportFileChooser chooser = createExportFileChooser(file);
		int result = chooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFileOrError();
			if (file != null) {
				config.setExportFile(file);
			}
		}

		updateDataInFrame();
	}

	/**
	 * Informs user how to see done keywords.
	 */
	public void viewDone() {
		info("To see result open file " + config.getExportFile().getPath()
				+ " (in excel?).");
	}

	/**
	 * Creates file chooser for import.
	 * 
	 * @return
	 */
	private JFileChooser createImportFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		FileFilter filterLines = new TxtWithSeparatorFileFilter(
				"Text file (*.txt), each keyword on particular line", "\n");
		FileFilter filterComa = new TxtWithSeparatorFileFilter(
				"Text file (*.txt), keywords separated by , (comma)", "\\, *");
		FileFilter filterSemicolon = new TxtWithSeparatorFileFilter(
				"Text file (*.txt), keywords separated by ; (semicolon)",
				"\\; *");
		chooser.addChoosableFileFilter(filterLines);
		chooser.addChoosableFileFilter(filterComa);
		chooser.addChoosableFileFilter(filterSemicolon);
		chooser.setFileFilter(filterComa);

		return chooser;
	}

	/**
	 * Creates file chooser for export.
	 * 
	 * @param selectedFile
	 * @return
	 */
	private JExportFileChooser createExportFileChooser(File selectedFile) {
		Set<AbstractExporter> exporters = KHMain.getExporters(config);

		JExportFileChooser chooser = new JExportFileChooser(exporters,
				selectedFile);

		return chooser;
	}

	private void error(String message) {
		JOptionPane.showMessageDialog(this, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void info(String message) {
		JOptionPane.showMessageDialog(this, message, "Mesage",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private class StopThread extends Thread {

		public StopThread(JMainFrame frame) {
			super("StopButtT");
		}

		public void run() {
			stopButt.setText("Stopping, wait...");
			stopButt.setEnabled(false);

			try {
				EventQueue.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						harvester.stop();
						harvester = null;

						updateThread.interrupt();
						updateThread = null;

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			data.saveToDumpFile(config);

			updateDataInFrame();
			setEnabledButts();
		}
	}

	private class StartThread extends Thread {
		private final JMainFrame frame;

		public StartThread(JMainFrame frame) {
			super("StartButtT");
			this.frame = frame;
		}

		public void run() {
			if (data == null) {
				error("No keywords to process. Click to 'Edit keywords', 'Import' or 'Open previous harvest'.");
				startButt.setEnabled(true);
				return;
			}

			editDial.setVisible(false);

			harvester = new ParalellHarvester(config,
					KHMain.getServices(config), KHMain.getExporters(config));
			boolean succ = harvester.start(data);
			if (!succ) {
				error("Some error occured during harvesting start.");
				harvester = null;
				return;
			}

			updateThread = new FormDataUpdateThread(frame, config);
			updateThread.start();

			updateDataInFrame();
			setEnabledButts();
		}
	}

	public class ViewDoneButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			viewDone();
		}

	}

	public class EditQueueButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			editKeywords();
		}

	}

	public class ChangeFileButtActionListner implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			chooseExportFile();
		}

	}

	public class LoadPreviousButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loadPreviousHarvest();
		}

	}

	public class ImportKeywordsButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			importKeywords();
		}

	}

	public class StopButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			stop();
		}

	}

	public class StartButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			start();
		}

	}
}

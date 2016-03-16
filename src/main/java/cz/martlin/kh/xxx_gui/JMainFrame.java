package cz.martlin.kh.xxx_gui;

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
import cz.martlin.kh.StuffProvider;
import cz.martlin.kh.KHMain.LoggingUncaughtExceptionHandler;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.export.AbstractEI;
import cz.martlin.kh.logic.harvest2.TreeHarvestThread;
import cz.martlin.kh.logic.harvest2.TreeHarvestProcessData;
import cz.martlin.kh.logic.harvest2.TreeRelKeywsHarvest;

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

	private TreeHarvestThread harvThread;
	private TreeHarvestProcessData data;

	private final JEditKeywordsDial editDial;

	private JButton startButt;
	private JButton stopButt;
	private JButton loadPrevButt;
	private JButton importButt;
	private JButton loadBackupButt;
	private JButton importExportedButt;
	private JLabel outputFileLbl;
	private JButton changeFileButt;
	private JLabel waitingLbl;
	private JButton editQueueButt;
	private JLabel doneLbl;
	private JButton viewDoneButt;
	private JLabel statusLbl;

	public JMainFrame(Config config) {
		super(TITLE);
		this.config = config;
		this.editDial = new JEditKeywordsDial(this, config);

		initializeComponents();

		updateDataInFrame();

		Dimension size = new Dimension(500, 350);
		setPreferredSize(size);
		setMinimumSize(size);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	private void initializeComponents() {
		JPanel content = new JPanel();
		initMainPane(content);
		getContentPane().add(content, BorderLayout.CENTER);

		JLabel footer = new JLabel("<html><i>" + KHMain.getAbout() + "</i></html>");
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

		// load previous and import butts
		loadPrevButt = new JButton("Open previous harvest");
		loadPrevButt.addActionListener(new LoadPreviousButtActionListener());
		pane.add(loadPrevButt);

		importExportedButt = new JButton("Import exported");
		importExportedButt.addActionListener(new ImportExportedButtActionListener());
		pane.add(importExportedButt);

		loadBackupButt = new JButton("Open backup of previous");
		loadBackupButt.addActionListener(new LoadBackupButtActionListener());
		pane.add(loadBackupButt);

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
		outputFileLbl = new JLabel("Output file: --" + config.getExExportFile().getPath());
		outputFileLbl.setPreferredSize(LEFT_LABEL_PREF_SIZE);
		outputFileLbl.setToolTipText(config.getExExportFile().getAbsolutePath());
		pane.add(outputFileLbl);

		changeFileButt = new JButton("Change file");
		changeFileButt.addActionListener(new ChangeFileButtActionListner());
		pane.add(changeFileButt);

		statusLbl = new JLabel("");
		pane.add(statusLbl);
		pane.add(new JLabel());

		SpringUtilities.makeCompactGrid(pane, 7, 2, 10, 10, 10, 15);

	}

	private boolean isRunning() {
		return harvThread != null;
	}

	/**
	 * Updates data shown in frame by {@link #data} and {@link #config}.
	 */
	public void updateDataInFrame() {
		stopButt.setText("Stop");

		String path = config.getExExportFile().getPath();
		outputFileLbl.setText("Output file: " + path);

		if (data == null) {
			waitingLbl.setText("Waiting to be processed: " + "Nothing");
			doneLbl.setText("Done: " + "Nothing");
		} else {
			int waiting = data.getWaitingsCount();
			waitingLbl.setText("Waiting to be processed: " + waiting);

			int done = data.getDoneCount();
			doneLbl.setText("Done: " + done);
		}

		setEnabledButts();
	}

	private void setEnabledButts() {
		boolean isRunning = isRunning();

		startButt.setEnabled(!isRunning);
		stopButt.setEnabled(isRunning);

		loadPrevButt.setEnabled(!isRunning);
		importButt.setEnabled(!isRunning);

		loadBackupButt.setEnabled(!isRunning);
		importExportedButt.setEnabled(!isRunning);

		changeFileButt.setEnabled(!isRunning);
		editQueueButt.setEnabled(!isRunning);
		viewDoneButt.setEnabled(!isRunning);
	}

	/**
	 * Sets status of status field
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		updateDataInFrame();
		this.statusLbl.setText(status);

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
			data = TreeHarvestProcessData.createNew(config, Collections.<String> emptySet());
		}

		editDial.setToData(data);
		editDial.setVisible(true);
	}

	/**
	 * Tries to load previously saved harvest (by
	 * {@link Config#getQueuesDumpFile()}).
	 */
	public void loadPreviousHarvest() {
		TreeHarvestProcessData newData = TreeHarvestProcessData.loadFromDumpFile(config, false);
		if (newData == null) {
			error("Could not load previous harvest. Check file " + config.getHwDataDumpFile().getPath());
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
			TxtWithSeparatorFileFilter filter = (TxtWithSeparatorFileFilter) chooser.getFileFilter();
			TreeHarvestProcessData newData = TreeHarvestProcessData.importThem(config, file, filter.getSeparator());
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
		File file = config.getExExportFile();

		JExportFileChooser chooser = createExportFileChooser(file);
		int result = chooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFileOrError();
			if (file != null) {
				config.setExExportFile(file);
			}
		}

		updateDataInFrame();
	}

	/**
	 * Informs user how to see done keywords.
	 */
	public void viewDone() {
		info("To see result open file " + config.getExExportFile().getPath() + " (in excel?).");
	}

	/**
	 * Imports files from exported.
	 */
	public void importExported() {
		TreeHarvestProcessData newData = TreeHarvestProcessData.loadExported(config);
		if (newData == null) {
			error("Could not load exported keywords. Try to check file " + config.getExExportFile().getPath());
			return;
		}

		data = newData;
		updateDataInFrame();
	}

	/**
	 * Imports keywords from backup of keywords.
	 */
	public void loadBackupOfPrevious() {
		TreeHarvestProcessData newData = TreeHarvestProcessData.loadFromDumpFile(config, true);
		if (newData == null) {
			error("Could not load backup of previous harvest. Check file "
					+ config.getHwDataDumpBackupFile().getPath());
			return;
		}

		data = newData;
		updateDataInFrame();
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

		FileFilter filterLines = new TxtWithSeparatorFileFilter("Text file (*.txt), each keyword on particular line",
				"\n");
		FileFilter filterComa = new TxtWithSeparatorFileFilter("Text file (*.txt), keywords separated by , (comma)",
				"\\, *");
		FileFilter filterSemicolon = new TxtWithSeparatorFileFilter(
				"Text file (*.txt), keywords separated by ; (semicolon)", "\\; *");
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
		Set<AbstractEI> exporters = StuffProvider.getExporters(config);

		JExportFileChooser chooser = new JExportFileChooser(exporters, selectedFile);

		return chooser;
	}

	private void error(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void info(String message) {
		JOptionPane.showMessageDialog(this, message, "Mesage", JOptionPane.INFORMATION_MESSAGE);
	}

	private class StopThread extends Thread {

		public StopThread(JMainFrame frame) {
			super("StopButtT");
			setUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		}

		public void run() {
			stopButt.setText("Stopping, wait...");
			stopButt.setEnabled(false);

			try {
				EventQueue.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						harvThread.interrupt();
						harvThread = null;
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
		//private final JMainFrame frame;

		public StartThread(JMainFrame frame) {
			super("StartButtT");
			//this.frame = frame;
			setUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		}

		public void run() {
			if (data == null) {
				error("No keywords to process. Click to 'Edit keywords', 'Import' or 'Open previous harvest'.");
				startButt.setEnabled(true);
				return;
			}

			editDial.setVisible(false);

			TreeRelKeywsHarvest harvester = new TreeRelKeywsHarvest(config);
			// StuffProvider.createHarvester(config, frame);
			harvThread = new TreeHarvestThread(harvester, data);
			harvThread.run();

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

	public class LoadBackupButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loadBackupOfPrevious();
		}

	}

	public class ImportExportedButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			importExported();
		}

	}

}

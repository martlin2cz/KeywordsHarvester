package cz.martlin.kh.gui;

import java.io.File;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cz.martlin.kh.logic.export.AbstractExporter;

public class JExportFileChooser extends JFileChooser {

	private static final long serialVersionUID = 5550587803546176078L;

	private final Set<AbstractExporter> exporters;

	public JExportFileChooser(Set<AbstractExporter> exporters, File selectedFile) {
		super();
		this.exporters = exporters;

		init(selectedFile);

	}

	private void init(File selectedFile) {
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setAcceptAllFileFilterUsed(false);

		for (AbstractExporter exporter : exporters) {
			FileFilter filter = new FileNameExtensionFilter(
					exporter.getFormatDescription(), exporter.getSuffix());

			addChoosableFileFilter(filter);
		}

		setSelectedFile(selectedFile);
	}

	public File getSelectedFileOrError() {
		File selected = super.getSelectedFile();

		AbstractExporter exporter = AbstractExporter.getBySuffix(exporters,
				selected);

		if (exporter == null) {
			JOptionPane.showMessageDialog(this,
					"Choose file with correct suffix", "Baf file format",
					JOptionPane.ERROR_MESSAGE);
			return null;
		} else {
			return selected;
		}
	}

}

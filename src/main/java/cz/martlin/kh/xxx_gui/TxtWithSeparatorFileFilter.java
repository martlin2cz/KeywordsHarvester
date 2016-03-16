package cz.martlin.kh.xxx_gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Implements file filter which accepts only text files and uses various (
 * {@link #getSeparator()}) separators for splitting items.
 * 
 * @author martin
 * 
 */
public class TxtWithSeparatorFileFilter extends FileFilter {

	private static final String EXT2 = "txt";
	private static final String EXT1 = "TXT";

	private final FileNameExtensionFilter subfilter;
	private final String separator;

	public TxtWithSeparatorFileFilter(String desc, String separator) {
		subfilter = new FileNameExtensionFilter(desc, EXT1, EXT2);
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	@Override
	public boolean accept(File f) {
		return subfilter.accept(f);
	}

	@Override
	public String getDescription() {
		return subfilter.getDescription();
	}

}

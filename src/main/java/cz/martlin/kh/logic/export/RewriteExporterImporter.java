package cz.martlin.kh.logic.export;

import java.io.IOException;
import java.util.Set;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.Keyword;

/**
 * Extends abstract exporter as exporter, which in each export completely
 * rewrites whole file. So, in each export ({@link #export(Set)}) opens file,
 * exports header, exports given keywords and closes file.
 * 
 * @author martin
 * 
 */
public abstract class RewriteExporterImporter extends AbstractEI {

	public RewriteExporterImporter(Config config) {
		super(config);
	}

	@Override
	public void initializeExporterToWrite() throws IOException {
		tryBackupFile();

		log.info("Will rewritelly export {} to {}.", getSuffix(), config
				.getExExportFile().getAbsoluteFile());
	}

	@Override
	public void finishExporterToWrite() throws IOException {
		log.info("Rewriting export closed.");
	}

	@Override
	public void export(Set<Keyword> allKeywords) throws IOException {
		openFileToWrite();

		exportHeaderOrShit();

		exportKeywords(allKeywords);

		closeFileToWrite();
	}

	// ////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void initializeExporterToRead() throws IOException {
		log.info("Will import from {}.", config.getExExportFile()
				.getAbsoluteFile());
	}

	@Override
	public void finishExporterToRead() throws IOException {
		log.info("Rewriting import closed.");
	}
	
	

}

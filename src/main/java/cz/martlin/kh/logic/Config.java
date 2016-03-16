package cz.martlin.kh.logic;

import java.io.File;
import java.io.Serializable;

/**
 * Application configuration.
 * 
 * @author martin
 * 
 */
public class Config implements Serializable/*, JaxonSerializable*/ {
	private static final long serialVersionUID = -6585645414753714922L;

	/**
	 * Time step in miliseconds which is used in waiting.
	 */
	private int waitStep = 100;
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Number of subkeywording images processed in each service.
	 */
	private int skSamplesCount = 1;

	/**
	 * Time in miliseconds to wait before next subkeyword service will be
	 * requested.
	 */
	private int skWaitBetweenServices = 1 * 1000;

	/**
	 * Shutterstock client id (required to use API).
	 */
	private String ssClientID = "60e093aa3cc83d1143c6";
	/**
	 * Shutterstock client secret (required to use API).
	 */
	private String ssClientSecret = "1aac92eb001e1cb2aec0f6739a9ecabe16e35d05";

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * Picworkflow wait-to-response time (in miliseconds).
	 */
	private int pwQueryTimeout = 10 * 1000;

	/**
	 * File with files that have not been successfully processed by picworkflow.
	 */
	private File pwFailedFile = new File("picworkflow-failed.csv");

	/**
	 * Time to wait when picworkflow successfully processes 0 keywords.
	 */
	private int pwFailedWait = 10 * 60 * 1000;

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * File to do export.
	 */
	private File exExportFile = new File("output.xlsx");

	// /////////////////////////////////////////////////////////////////////////
	private int hwProcessIterationSize = 60;

	/**
	 * File to dump of process data for future load.
	 */
	private File hwDataDumpFile = new File("dump.bin");

	/**
	 * File to be overriden by {@link #hwDataDumpFile} before is written.
	 */
	private File hwDataDumpBackupFile = new File("dump_backup.bin");

	// /////////////////////////////////////////////////////////////////////////

	public Config() {
		super();
	}

	public int getWaitStep() {
		return waitStep;
	}

	public void setWaitStep(int waitStep) {
		this.waitStep = waitStep;
	}

	public int getSkSamplesCount() {
		return skSamplesCount;
	}

	public void setSkSamplesCount(int skSamplesCount) {
		this.skSamplesCount = skSamplesCount;
	}

	public int getSkWaitBetweenServices() {
		return skWaitBetweenServices;
	}

	public void setSkWaitBetweenServices(int skWaitBetweenServices) {
		this.skWaitBetweenServices = skWaitBetweenServices;
	}

	public String getSsClientID() {
		return ssClientID;
	}

	public void setSsClientID(String ssClientID) {
		this.ssClientID = ssClientID;
	}

	public String getSsClientSecret() {
		return ssClientSecret;
	}

	public void setSsClientSecret(String ssClientSecret) {
		this.ssClientSecret = ssClientSecret;
	}

	public int getPwQueryTimeout() {
		return pwQueryTimeout;
	}

	public void setPwQueryTimeout(int pwQueryTimeout) {
		this.pwQueryTimeout = pwQueryTimeout;
	}

	public File getPwFailedFile() {
		return pwFailedFile;
	}

	public void setPwFailedFile(File pwFailedFile) {
		this.pwFailedFile = pwFailedFile;
	}

	public int getPwFailedWait() {
		return pwFailedWait;
	}

	public void setPwFailedWait(int pwFailedWait) {
		this.pwFailedWait = pwFailedWait;
	}

	public File getExExportFile() {
		return exExportFile;
	}

	public void setExExportFile(File exExportFile) {
		this.exExportFile = exExportFile;
	}

	public int getHwProcessIterationSize() {
		return hwProcessIterationSize;
	}

	public void setHwProcessIterationSize(int hwProcessIterSize) {
		this.hwProcessIterationSize = hwProcessIterSize;
	}

	public File getHwDataDumpFile() {
		return hwDataDumpFile;
	}

	public void setHwDataDumpFile(File hwDataDumpFile) {
		this.hwDataDumpFile = hwDataDumpFile;
	}

	public File getHwDataDumpBackupFile() {
		return hwDataDumpBackupFile;
	}

	public void setHwDataDumpBackupFile(File hwDataDumpBackupFile) {
		this.hwDataDumpBackupFile = hwDataDumpBackupFile;
	}

	public void setTo(Config other) {
		waitStep = other.waitStep;
		skSamplesCount = other.skSamplesCount;
		skWaitBetweenServices = other.skWaitBetweenServices;
		ssClientID = other.ssClientID;
		ssClientSecret = other.ssClientSecret;
		pwQueryTimeout = other.pwQueryTimeout;
		pwFailedFile = other.pwFailedFile;
		pwFailedWait = other.pwFailedWait;
		exExportFile = other.exExportFile;
		hwProcessIterationSize = other.hwProcessIterationSize;
		hwDataDumpFile = other.hwDataDumpFile;
		hwDataDumpBackupFile = other.hwDataDumpBackupFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((exExportFile == null) ? 0 : exExportFile.hashCode());
		result = prime
				* result
				+ ((hwDataDumpBackupFile == null) ? 0 : hwDataDumpBackupFile
						.hashCode());
		result = prime * result
				+ ((hwDataDumpFile == null) ? 0 : hwDataDumpFile.hashCode());
		result = prime * result + hwProcessIterationSize;
		result = prime * result
				+ ((pwFailedFile == null) ? 0 : pwFailedFile.hashCode());
		result = prime * result + pwFailedWait;
		result = prime * result + pwQueryTimeout;
		result = prime * result + skSamplesCount;
		result = prime * result + skWaitBetweenServices;
		result = prime * result
				+ ((ssClientID == null) ? 0 : ssClientID.hashCode());
		result = prime * result
				+ ((ssClientSecret == null) ? 0 : ssClientSecret.hashCode());
		result = prime * result + waitStep;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Config other = (Config) obj;
		if (exExportFile == null) {
			if (other.exExportFile != null)
				return false;
		} else if (!exExportFile.equals(other.exExportFile))
			return false;
		if (hwDataDumpBackupFile == null) {
			if (other.hwDataDumpBackupFile != null)
				return false;
		} else if (!hwDataDumpBackupFile.equals(other.hwDataDumpBackupFile))
			return false;
		if (hwDataDumpFile == null) {
			if (other.hwDataDumpFile != null)
				return false;
		} else if (!hwDataDumpFile.equals(other.hwDataDumpFile))
			return false;
		if (hwProcessIterationSize != other.hwProcessIterationSize)
			return false;
		if (pwFailedFile == null) {
			if (other.pwFailedFile != null)
				return false;
		} else if (!pwFailedFile.equals(other.pwFailedFile))
			return false;
		if (pwFailedWait != other.pwFailedWait)
			return false;
		if (pwQueryTimeout != other.pwQueryTimeout)
			return false;
		if (skSamplesCount != other.skSamplesCount)
			return false;
		if (skWaitBetweenServices != other.skWaitBetweenServices)
			return false;
		if (ssClientID == null) {
			if (other.ssClientID != null)
				return false;
		} else if (!ssClientID.equals(other.ssClientID))
			return false;
		if (ssClientSecret == null) {
			if (other.ssClientSecret != null)
				return false;
		} else if (!ssClientSecret.equals(other.ssClientSecret))
			return false;
		if (waitStep != other.waitStep)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Config [waitStep=" + waitStep + ", skSamplesCount="
				+ skSamplesCount + ", skWaitBetweenServices="
				+ skWaitBetweenServices + ", ssClientID=" + ssClientID
				+ ", ssClientSecret=" + ssClientSecret + ", pwQueryTimeout="
				+ pwQueryTimeout + ", pwFailedFile=" + pwFailedFile
				+ ", pwFailedWait=" + pwFailedWait + ", exExportFile="
				+ exExportFile + ", hwProcessIterationSize="
				+ hwProcessIterationSize + ", hwDataDumpFile=" + hwDataDumpFile
				+ ", hwDataDumpBackupFile=" + hwDataDumpBackupFile + "]";
	}

	/**
	 * Creates new Config.
	 * 
	 * @return
	 */
	public static Config loadOrDefault() {
		Config config = new Config();


		return config;
	}

//	@Override
//	public String jaxonDescription() {
//		return "Keywords Harvester configuration";
//	}

}

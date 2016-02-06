package cz.martlin.kh.logic;

import java.io.File;

import cz.martlin.kh.logic.utils.ConfigStorerLoader;

/**
 * Application configuration.
 * 
 * @author martin
 * 
 */
public class Config {

	/**
	 * Time step in miliseconds which is used in waiting.
	 */
	private int waitStep = 100;
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Number of subkeywording images processed in each service.
	 */
	private int samplesCount = 1;

	/**
	 * Time in miliseconds to wait before next subkeyword service will be
	 * requested.
	 */
	private int subkeywWaitBetweenServices = 1 * 1000;

	/**
	 * Shutterstock client id (required to use API).
	 */
	private String ssClientid = "60e093aa3cc83d1143c6";
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
	 * Number of keywords to send to picworkflow in one request.
	 */
	private int pwBatchSize = 50;

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
	 * (Minimal) time (in miliseconds) to wait between subkeywording queries.
	 */
	private int waitBtwSubkeywordingQrs = 60 * 1000;
	/**
	 * (Minimal) time (in miliseconds) to wait between picworkflow queries.
	 */
	private int waitBtwPicflowQrs = 60 * 1000;
	/**
	 * (Minimal) time (in miliseconds) to wait between exports.
	 */
	private int waitBtwExports = 10 * 1000;

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * Count of keywords to Picworkflow query before is query executed.
	 */
	private int hwToPicworkflowQueueSize = 50;
	/**
	 * Count of keywords to export before is export executed.
	 */
	private int hwToExportQueueSize = 50;

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Count of keywords to export in one batch.
	 */
	private int exportBatchSize = 50;

	/**
	 * File to do export.
	 */
	private File exportFile = new File("output.xlsx");

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * File to dump queues for future load.
	 */
	private File queuesDumpFile = new File("dump.bin");

	/**
	 * How offten (in miliseconds) reload informations in main frame.
	 */
	private long formUpdateInterval = 2 * 1000;

	/**
	 * How offted do garbage collection. Currently <strong>not used</strong>.
	 * 
	 * @deprecated
	 */
	private long memoryCleanInterval = 60 * 1000;

	// /////////////////////////////////////////////////////////////////////////

	public Config() {
		super();
	}

	public int getSKWaitBetweenServices() {
		return subkeywWaitBetweenServices;
	}

	public String getSScliendID() {
		return ssClientid;
	}

	public String getSSclientSecret() {
		return ssClientSecret;
	}

	public long getWaitStep() {
		return waitStep;
	}

	public long getPWQueryTimeout() {
		return pwQueryTimeout;
	}

	public int getSamplesCount() {
		return samplesCount;
	}

	public int getPWBatchSize() {
		return pwBatchSize;
	}

	public File getPwFailedFile() {
		return pwFailedFile;
	}

	public int getPwFailedWait() {
		return pwFailedWait;
	}

	public int getHWToPicworkflowQueueSize() {
		return hwToPicworkflowQueueSize;
	}

	public int getHWToExportQueueSize() {
		return hwToExportQueueSize;
	}

	public int getExExportBatchSize() {
		return exportBatchSize;
	}

	public long getWaitBetweenSubkeywordQueries() {
		return waitBtwSubkeywordingQrs;
	}

	public long getWaitBetweenPicflowQueries() {
		return waitBtwPicflowQrs;
	}

	public long getWaitBetweenExports() {
		return waitBtwExports;
	}

	public File getExportFile() {
		return exportFile;
	}

	public File getQueuesDumpFile() {
		return queuesDumpFile;
	}

	public long getFormUpdateInterval() {
		return formUpdateInterval;
	}

	public long getMemoryCleanInterval() {
		return memoryCleanInterval;
	}

	public void setSKWaitBetweenServices(int subkeywWaitBetweenServices) {
		this.subkeywWaitBetweenServices = subkeywWaitBetweenServices;
	}

	public void setSsClientid(String ssClientid) {
		this.ssClientid = ssClientid;
	}

	public void setSsClientSecret(String ssClientSecret) {
		this.ssClientSecret = ssClientSecret;
	}

	public void setWaitStep(int waitStep) {
		this.waitStep = waitStep;
	}

	public void setPwQueryTimeout(int pwQueryTimeout) {
		this.pwQueryTimeout = pwQueryTimeout;
	}

	public void setSamplesCount(int samplesCount) {
		this.samplesCount = samplesCount;
	}

	public void setPwBatchSize(int pwBatchCount) {
		this.pwBatchSize = pwBatchCount;
	}

	public void setPwFailedFile(File pwFailedFile) {
		this.pwFailedFile = pwFailedFile;
	}

	public void setPwFailedWait(int pwFailedWait) {
		this.pwFailedWait = pwFailedWait;
	}

	public void setHwToPicworkflowQueueSize(int hwToPicworkflowQuerySize) {
		this.hwToPicworkflowQueueSize = hwToPicworkflowQuerySize;
	}

	public void setExportQueueSize(int exportQueueSize) {
		this.hwToExportQueueSize = exportQueueSize;
	}

	public void setExportBatchSize(int exportBatchSize) {
		this.exportBatchSize = exportBatchSize;
	}

	public void setWaitBtwSubkeywordingQrs(int waitBtwSubkeywordingQrs) {
		this.waitBtwSubkeywordingQrs = waitBtwSubkeywordingQrs;
	}

	public void setWaitBtwPicflowQrs(int waitBtwPicflowQrs) {
		this.waitBtwPicflowQrs = waitBtwPicflowQrs;
	}

	public void setWaitBtwExports(int waitBtwExports) {
		this.waitBtwExports = waitBtwExports;
	}

	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}

	public void setQueuesDumpFile(File queuesDumpFile) {
		this.queuesDumpFile = queuesDumpFile;
	}

	public void setFormUpdateInterval(long formUpdateInterval) {
		this.formUpdateInterval = formUpdateInterval;
	}

	public void setMemoryCleanInterval(long memoryCleanInterval) {
		this.memoryCleanInterval = memoryCleanInterval;
	}

	/**
	 * Tries to load configuration from file. If succeeds, returns it. If not,
	 * returns default instance.
	 * 
	 * @return
	 */
	public static Config loadOrDefault() {
		Config config = new Config();

		ConfigStorerLoader csl = new ConfigStorerLoader();
		csl.load(config);

		return config;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + exportBatchSize;
		result = prime * result
				+ ((exportFile == null) ? 0 : exportFile.hashCode());
		result = prime * result
				+ (int) (formUpdateInterval ^ (formUpdateInterval >>> 32));
		result = prime * result + hwToExportQueueSize;
		result = prime * result + hwToPicworkflowQueueSize;
		result = prime * result
				+ (int) (memoryCleanInterval ^ (memoryCleanInterval >>> 32));
		result = prime * result + pwBatchSize;
		result = prime * result
				+ ((pwFailedFile == null) ? 0 : pwFailedFile.hashCode());
		result = prime * result + pwFailedWait;
		result = prime * result + pwQueryTimeout;
		result = prime * result
				+ ((queuesDumpFile == null) ? 0 : queuesDumpFile.hashCode());
		result = prime * result + samplesCount;
		result = prime * result
				+ ((ssClientSecret == null) ? 0 : ssClientSecret.hashCode());
		result = prime * result
				+ ((ssClientid == null) ? 0 : ssClientid.hashCode());
		result = prime * result + waitBtwExports;
		result = prime * result + waitBtwPicflowQrs;
		result = prime * result + waitBtwSubkeywordingQrs;
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
		if (exportBatchSize != other.exportBatchSize)
			return false;
		if (exportFile == null) {
			if (other.exportFile != null)
				return false;
		} else if (!exportFile.equals(other.exportFile))
			return false;
		if (formUpdateInterval != other.formUpdateInterval)
			return false;
		if (hwToExportQueueSize != other.hwToExportQueueSize)
			return false;
		if (hwToPicworkflowQueueSize != other.hwToPicworkflowQueueSize)
			return false;
		if (memoryCleanInterval != other.memoryCleanInterval)
			return false;
		if (pwBatchSize != other.pwBatchSize)
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
		if (queuesDumpFile == null) {
			if (other.queuesDumpFile != null)
				return false;
		} else if (!queuesDumpFile.equals(other.queuesDumpFile))
			return false;
		if (samplesCount != other.samplesCount)
			return false;
		if (ssClientSecret == null) {
			if (other.ssClientSecret != null)
				return false;
		} else if (!ssClientSecret.equals(other.ssClientSecret))
			return false;
		if (ssClientid == null) {
			if (other.ssClientid != null)
				return false;
		} else if (!ssClientid.equals(other.ssClientid))
			return false;
		if (waitBtwExports != other.waitBtwExports)
			return false;
		if (waitBtwPicflowQrs != other.waitBtwPicflowQrs)
			return false;
		if (waitBtwSubkeywordingQrs != other.waitBtwSubkeywordingQrs)
			return false;
		if (waitStep != other.waitStep)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Config [waitStep=" + waitStep + ", samplesCount="
				+ samplesCount + ", ssClientid=" + ssClientid
				+ ", ssClientSecret=" + ssClientSecret + ", pwQueryTimeout="
				+ pwQueryTimeout + ", pwBatchSize=" + pwBatchSize
				+ ", pwFailedFile=" + pwFailedFile + ", pwFailedWait="
				+ pwFailedWait + ", waitBtwSubkeywordingQrs="
				+ waitBtwSubkeywordingQrs + ", waitBtwPicflowQrs="
				+ waitBtwPicflowQrs + ", waitBtwExports=" + waitBtwExports
				+ ", hwToPicworkflowQueueSize=" + hwToPicworkflowQueueSize
				+ ", hwToExportQueueSize=" + hwToExportQueueSize
				+ ", exportBatchSize=" + exportBatchSize + ", exportFile="
				+ exportFile + ", queuesDumpFile=" + queuesDumpFile
				+ ", formUpdateInterval=" + formUpdateInterval
				+ ", memoryCleanInterval=" + memoryCleanInterval + "]";
	}

}

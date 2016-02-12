package cz.martlin.kh.logic.harvest2;

/**
 * Interface for very simple reporting what happened in harvester (primarly
 * {@link TreeRelKeywsHarvest}).
 * 
 * @author martin
 * 
 */
public interface HarvestingListener {

	/**
	 * Somehow reports that "what" happened in harvester (i.e.
	 * "Harvesting started" or "picworkflowing 99 keywords...").
	 * 
	 * @param what
	 */
	public void occured(String what);
}

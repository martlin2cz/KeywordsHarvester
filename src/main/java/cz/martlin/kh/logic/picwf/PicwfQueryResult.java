package cz.martlin.kh.logic.picwf;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import cz.martlin.kh.logic.Keyword;

//TODO doc
public class PicwfQueryResult {
	private final Set<Keyword> metadatas;
	private final Set<String> requested;
	private final Set<String> done;
	private final Set<String> notdone;

	// private final Set<String> notdone;

	public PicwfQueryResult(Set<String> requested, Set<Keyword> resulted) {
		this.metadatas = resulted;
		this.requested = requested;
		this.done = calculateDone(resulted);
		this.notdone = calculateNotdone(requested, done);
	}

	public Set<Keyword> getMetadatas() {
		return metadatas;
	}

	public Set<String> getRequested() {
		return requested;
	}

	public Set<String> getDone() {
		return done;
	}

	public Set<String> getNotdone() {
		return notdone;
	}

	public double getSuccessRatio() {
		return ((double) done.size()) / requested.size();
	}

	public boolean isNoSuccessfulyDone() {
		return done.isEmpty();
	}

	public boolean isSomeNotSuccessfulyDone() {
		return !notdone.isEmpty();
	}

	public int getRequestedCount() {
		return requested.size();
	}

	public int getDoneCount() {
		return done.size();
	}

	public int getNotdoneCount() {
		return notdone.size();
	}

	public Set<String> getReallyNotdone() {
		Set<String> result = new HashSet<>(getNotdoneCount());
		Set<String> doneCI = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		Set<String> notdoneCI = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		doneCI.addAll(done);
		notdoneCI.addAll(notdone);

		for (String request : requested) {
			boolean isDone = doneCI.contains(request);
			boolean isNotDone = notdoneCI.contains(request);

			if (!isDone && isNotDone) {
				result.add(request);
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return "PicwfQueryResult [getSuccessRatio()=" + getSuccessRatio()
				+ ", getRequestedCount()=" + getRequestedCount()
				+ ", getDoneCount()=" + getDoneCount() + ", getNotdoneCount()="
				+ getNotdoneCount() + ", metadatas=" + metadatas
				+ ", requested=" + requested + ", done=" + done + ", notdone="
				+ notdone + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((done == null) ? 0 : done.hashCode());
		result = prime * result
				+ ((metadatas == null) ? 0 : metadatas.hashCode());
		result = prime * result + ((notdone == null) ? 0 : notdone.hashCode());
		result = prime * result
				+ ((requested == null) ? 0 : requested.hashCode());
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
		PicwfQueryResult other = (PicwfQueryResult) obj;
		if (done == null) {
			if (other.done != null)
				return false;
		} else if (!done.equals(other.done))
			return false;
		if (metadatas == null) {
			if (other.metadatas != null)
				return false;
		} else if (!metadatas.equals(other.metadatas))
			return false;
		if (notdone == null) {
			if (other.notdone != null)
				return false;
		} else if (!notdone.equals(other.notdone))
			return false;
		if (requested == null) {
			if (other.requested != null)
				return false;
		} else if (!requested.equals(other.requested))
			return false;
		return true;
	}

	private static Set<String> calculateDone(Set<Keyword> keywords) {
		Set<String> result = new LinkedHashSet<>(keywords.size());

		for (Keyword keyword : keywords) {
			result.add(keyword.getKeyword());
		}

		return result;
	}

	private static Set<String> calculateNotdone(Set<String> requested,
			Set<String> done) {

		if (requested.contains(null)) {
			System.err.println("REquest contains null: " + requested);
		}

		Set<String> result = new LinkedHashSet<>(requested);
		result.removeAll(done);
		return result;
	}

	public static Set<String> calculateNotdoneKeyws(Set<String> requested,
			Set<Keyword> doneKeyws) {
		Set<String> done = calculateDone(doneKeyws);
		return calculateNotdone(requested, done);
	}

}

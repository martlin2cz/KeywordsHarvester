package cz.martlin.kh.logic;

import java.io.Serializable;

public class Keyword implements Serializable {

	private static final long serialVersionUID = 112325173295239990L;

	private final String keyword;
	private final int lang;
	private final int count;
	private final int views;
	private final int downloads;
	private final double rating;

	public Keyword(String keyword, int lang, int count, int views,
			int downloads, double rating) {
		super();

		this.keyword = keyword;
		this.lang = lang;
		this.count = count;
		this.views = views;
		this.downloads = downloads;
		this.rating = rating;
	}

	public String getKeyword() {
		return keyword;
	}

	public int getLang() {
		return lang;
	}

	public int getCount() {
		return count;
	}

	public int getViews() {
		return views;
	}

	public int getDownloads() {
		return downloads;
	}

	public double getRating() {
		return rating;
	}

	public double getViewsPerFile() {
		return ((double) views) / count;
	}

	public double getDownloadsPerFile() {
		return ((double) downloads) / count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + downloads;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + lang;
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + views;
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
		Keyword other = (Keyword) obj;
		if (count != other.count)
			return false;
		if (downloads != other.downloads)
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (lang != other.lang)
			return false;
		if (Double.doubleToLongBits(rating) != Double
				.doubleToLongBits(other.rating))
			return false;
		if (views != other.views)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Keyword [keyword=" + keyword + ", lang=" + lang + ", count="
				+ count + ", views=" + views + ", downloads=" + downloads
				+ ", rating=" + rating + ", getViewsPerFile()="
				+ getViewsPerFile() + ", getDownloadsPerFile()="
				+ getDownloadsPerFile() + "]";
	}

}

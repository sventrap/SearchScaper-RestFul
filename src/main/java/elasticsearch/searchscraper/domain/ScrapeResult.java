package elasticsearch.searchscraper.domain;

public class ScrapeResult {

	private String url;
	private String title;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	public String toString() {
		return new StringBuilder(getTitle()).append(" ").append(getUrl()).toString();
	}
}

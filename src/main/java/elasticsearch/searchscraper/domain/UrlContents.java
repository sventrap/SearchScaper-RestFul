package elasticsearch.searchscraper.domain;

public class UrlContents {

	private String content;
	private int httpStatusCode;

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}


	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}


	/**
	 * @return the httpStatusCode
	 */
	public int getHttpStatusCode() {
		return httpStatusCode;
	}


	/**
	 * @param httpStatusCode the httpStatusCode to set
	 */
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}


	public String toString() {
		return new StringBuilder(getHttpStatusCode()).append("::").append(getContent()).toString();
	}
}

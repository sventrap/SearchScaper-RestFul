package elasticsearch.searchscraper.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SearchResult {

	private String taskName;
	private String content;
	private String title;
	private String httpStatusCode;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;

	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}


	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}


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


	/**
	 * @return the httpStatusCode
	 */
	public String getHttpStatusCode() {
		return httpStatusCode;
	}


	/**
	 * @param httpStatusCode the httpStatusCode to set
	 */
	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}


	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}


	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String toString() {
		return new StringBuilder(getTaskName()).append(" ").append(getTitle()).toString();
	}
}

package elasticsearch.searchscraper.service.api;

import java.util.List;

import elasticsearch.searchscraper.domain.SearchTask;

public interface SearchTaskService {
	void save(SearchTask searchTask) throws Exception;

	void deleteIndex() throws Exception;

	void createIndex() throws Exception;

	List<SearchTask> findByTaskName(String taskName) throws Exception;

	List<SearchTask> findActiveSearchTasks() throws Exception;
}

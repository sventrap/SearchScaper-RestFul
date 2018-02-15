package elasticsearch.searchscraper.service.api;

import java.util.List;

import elasticsearch.searchscraper.domain.SearchResult;

public interface SearchResultService {
	SearchResult save(SearchResult searchTask) throws Exception;

	void deleteIndex() throws Exception;

	void createIndex() throws Exception;

	SearchResult findByTaskName(String taskName) throws Exception;

	List<SearchResult> findAllSearchResults() throws Exception;
}

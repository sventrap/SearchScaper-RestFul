package elasticsearch.searchscraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import elasticsearch.searchscraper.service.api.SearchResultService;

@Service
public class SearchResultSetup{
	private static Logger logger = LoggerFactory.getLogger(SearchResultSetup.class);
	
	@Autowired
	SearchResultService searchResultService;

	public void init() throws Exception {

		searchResultService.deleteIndex();
		searchResultService.createIndex();
		
		logger.info("index created");
	}
}

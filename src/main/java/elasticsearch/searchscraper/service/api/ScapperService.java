package elasticsearch.searchscraper.service.api;

import java.util.List;

import elasticsearch.searchscraper.domain.ScrapeResult;
import elasticsearch.searchscraper.domain.UrlContents;

public interface ScapperService {
	
	List<ScrapeResult> scrape(String searchWord) throws Exception;
	
	UrlContents getContents(String url) throws Exception;

}

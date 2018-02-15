package elasticsearch.searchscraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import elasticsearch.searchscraper.domain.ScrapeResult;
import elasticsearch.searchscraper.domain.SearchResult;
import elasticsearch.searchscraper.domain.SearchTask;
import elasticsearch.searchscraper.domain.UrlContents;
import elasticsearch.searchscraper.service.api.ScapperService;
import elasticsearch.searchscraper.service.api.SearchResultService;
import elasticsearch.searchscraper.service.api.SearchTaskService;

@Service
public class SearchResultsProcessor{
	private static Logger logger = LoggerFactory.getLogger(SearchResultsProcessor.class);
	
	@Autowired
	SearchTaskService searchTaskService;

	@Autowired
	SearchResultService searchResultsService;

	@Autowired
	ScapperService scrapperService;

	@Scheduled(initialDelay=2000,fixedDelayString = "#{new Double((T(java.lang.Math).random() + 1) * 1000).intValue()}")
	public void process() throws Exception {

		List<SearchTask> searchTasks = searchTaskService.findActiveSearchTasks();
		logger.info("searchtasks returned {}", searchTasks);

		for (SearchTask searchTask: searchTasks) {
			String keywords = searchTask.getKeyWords();
			if(null != keywords){
				List<String> keywordList = new ArrayList<String>(Arrays.asList(keywords.split(",")));
				List<ScrapeResult> result = getScrapeResults(keywordList);
				processSearchResults(searchTask,result);
			}
		}
	}
	
	private List<ScrapeResult> getScrapeResults(List<String> keywords) throws Exception{
		List<ScrapeResult> results = new ArrayList<>();
		for(String keyword : keywords) {
			logger.info("Start Scrape for keyword {} ", keyword);
			List<ScrapeResult> scrapeResults = scrapperService.scrape(keyword);
			logger.info("scrape results {} ", scrapeResults);
			results.addAll(scrapeResults);
		}
		return results;
	}
	
	private void processSearchResults(SearchTask searchTask, List<ScrapeResult> results) throws Exception {
		SearchResult searchResult;
		for(ScrapeResult scrapeResult: results){
			searchResult = new SearchResult();
			searchResult.setTaskName(searchTask.getTaskName());
			searchResult.setTitle(scrapeResult.getTitle());
			searchResult.setCreatedAt(Calendar.getInstance().getTime());
			
			String url = scrapeResult.getUrl();
			if(null != url && url.indexOf("webcache.googleusercontent.com") != -1){
				url = url.substring(url.indexOf("http",url.indexOf("http")+1));
				scrapeResult.setUrl(url);
			}
			
			UrlContents urlContent = scrapperService.getContents(scrapeResult.getUrl());
			searchResult.setHttpStatusCode(String.valueOf(urlContent.getHttpStatusCode()));
			searchResult.setContent(urlContent.getContent());
			searchResultsService.save(searchResult);
		}
	}
}

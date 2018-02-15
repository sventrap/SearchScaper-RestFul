package elasticsearch.searchscraper;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import elasticsearch.searchscraper.domain.SearchTask;
import elasticsearch.searchscraper.service.api.SearchTaskService;

@Service
public class SearchTaskSetup {
	private static Logger logger = LoggerFactory.getLogger(SearchTaskSetup.class);
	
	@Autowired
	SearchTaskService searchTaskService;

	public void init() throws Exception {

		searchTaskService.deleteIndex();
		searchTaskService.createIndex();

		addSearchTasks();
		
		List<SearchTask> searchTasks = searchTaskService.findActiveSearchTasks();
		logger.info("searchtasks returned {}", searchTasks);
	}
	
	private void addSearchTasks() throws Exception{
		//saveSearchTasks("centralized logging", "\"datadog\", \"metrics\", \"logging\"", true);
		//saveSearchTasks("relational databases", "\"postgres\", \"sql\", \"rbdms\", \"mysql\"", true);
		//saveSearchTasks("programming languages", "\"golang\", \"rust\", \"ruby\", \"python\", \"java\", \"clojure\"", true);
		saveSearchTasks("Olympics", "\"red gerard\",\"mikaela shiffrin\"", true);
	}

	private void saveSearchTasks(String taskName, String keyWords, boolean active) throws Exception{
		SearchTask searchTask = new SearchTask();
		searchTask.setCreatedAt(Calendar.getInstance().getTime());
		searchTask.setActive(active);
		searchTask.setTaskName(taskName);
		searchTask.setKeyWords(keyWords);

		searchTaskService.save(searchTask);
		logger.info("Successfully saved {}", searchTask.toString());
		
	}
}

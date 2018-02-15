package elasticsearch.searchscraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import elasticsearch.searchscraper.config.ElasticSearchConfiguration;

@SpringBootConfiguration
@EnableScheduling
@Import(value = ElasticSearchConfiguration.class)
public class ApplicationMain implements CommandLineRunner {
	private static Logger logger = LoggerFactory.getLogger(ApplicationMain.class);

	@Autowired
	SearchTaskSetup searchTaskSetup;

	@Autowired
	SearchResultSetup searchResultSetup;

	@Autowired
	SearchResultsProcessor searchResultsProcessor;

	public static void main(String args[]) {
		SpringApplication.run(ApplicationMain.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("############################################ Starting #########################################");
		logger.info("\n###############\nUsage \n {} \n {} \n {}\n###################",
				"java -jar SearchScaper-RestFul-0.0.1-SNAPSHOT-spring-boot.jar 1 : will create index and searchTask",
				"java -jar SearchScaper-RestFul-0.0.1-SNAPSHOT-spring-boot.jar 2 : will create index for searchresult",
				" if no option or other options then searchResults will be processed");
		
		if (args != null && args.length > 0) {
			String option = args[0];
			if ("1".equals(option)) {
				searchTaskSetup.init();
			} else if ("2".equals(option)) {
				searchResultSetup.init();
			} else {
				searchResultsProcessor.process();
			}
		} else{
			searchResultsProcessor.process();
		}
		
		logger.info("############################################ FINISHED #########################################");
	}
}

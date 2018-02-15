package elasticsearch.searchscraper;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import elasticsearch.searchscraper.config.ElasticSearchConfiguration;

@Configuration
@Import(ElasticSearchConfiguration.class)
public class TestConfiguration {

}

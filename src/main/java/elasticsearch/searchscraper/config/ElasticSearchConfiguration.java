package elasticsearch.searchscraper.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "elasticsearch.searchscraper", "elasticsearch.searchscraper.service.api",
		"elasticsearch.searchscraper.service.impl", "elasticsearch.searchscraper.ws.api",
		"elasticsearch.searchscraper.ws.impl" })
public class ElasticSearchConfiguration {

	@Bean(destroyMethod = "close")
	public RestHighLevelClient buildRestHighLevelClient() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
	}
}

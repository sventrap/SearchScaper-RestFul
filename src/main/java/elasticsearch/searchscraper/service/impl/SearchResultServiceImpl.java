package elasticsearch.searchscraper.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import elasticsearch.searchscraper.domain.SearchResult;
import elasticsearch.searchscraper.service.api.SearchResultService;

@Service
public class SearchResultServiceImpl implements SearchResultService {

	private static Logger logger = LoggerFactory.getLogger(SearchResultServiceImpl.class);
	
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Override
	public SearchResult save(SearchResult searchResult) throws Exception {
		// Convert the POJO to a JSON string
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(searchResult);

		logger.info("JSON representation of searchtask : {}",json);

		IndexRequest indexRequest = new IndexRequest("searchresults", "searchresult").source(json,XContentType.JSON);
		restHighLevelClient.index(indexRequest,  new BasicHeader("Content-Type", "application/json"));

		return findByTaskName(searchResult.getTaskName());
	}

	@Override
	public void createIndex() throws Exception {
		String mapping ="{\n" +
				"    \"searchresult\" : { \n" +
				"        \"properties\" : { \n" +
				"            \"taskName\" : { \n" +
				"				\"type\" : \"text\" \n" +
				"			},\n" +
				"            \"content\" : { \n" +
				"				\"type\" : \"text\" \n" +
				"			}, \n" +
				"            \"title\" : { \n" +
				"				\"type\" : \"text\" \n" +
				"			}, \n" +
				"            \"httpStatusCode\" : { \n" +
				"				\"type\" : \"integer\" \n" +
				"			},\n" +
				"            \"createdAt\" : { \n" +
				"				\"type\" : \"date\", \n" +
				"				\"format\": \"epoch_millis||dateOptionalTime\" \n" +
				"			}\n" +
				"		}\n" +			
				"    }\n" +
				"}";

		CreateIndexRequest request = new CreateIndexRequest("searchresults");
		request.mapping("searchresult", mapping, XContentType.JSON);

		restHighLevelClient.indices().create(request, new BasicHeader("Content-Type", "application/json"));
	}

	@Override
	public void deleteIndex() throws Exception {
		DeleteIndexRequest request = new DeleteIndexRequest("searchresults");
		request.indicesOptions(IndicesOptions.lenientExpandOpen());
		DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(request,
				new BasicHeader("Content-Type", "application/json"));
		logger.info("is ACK {}",deleteIndexResponse.isAcknowledged());
	}

	@Override
	public SearchResult findByTaskName(String taskName) throws Exception {
		
		SearchRequest searchRequest = new SearchRequest("searchresults");
		searchRequest.types("searchresult");
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		sourceBuilder.query(QueryBuilders.matchQuery("taskName", taskName)); 
		sourceBuilder.from(0); 
		sourceBuilder.size(5); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
		searchRequest = searchRequest.source(sourceBuilder);
		
		SearchResponse response = restHighLevelClient.search(searchRequest, new BasicHeader("Content-Type", "application/json"));
		
		logger.info("Status: {}", response.status().getStatus());
		logger.info("total hits {}",response.getHits().getTotalHits());
		
		if (response.getHits().getTotalHits() > 0) {
			logger.info("String returned {}", (response.getHits().getTotalHits() > 0
					? response.getHits().getAt(0).getSourceAsString() : "no hits"));
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(response.getHits().getAt(0).getSourceAsString(), SearchResult.class);
		} else {
			return null;
		}
	}

	@Override
	public List<SearchResult> findAllSearchResults() throws Exception {
		SearchRequest searchRequest = new SearchRequest("searchresults");
		searchRequest.types("searchresult");
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		sourceBuilder.query(QueryBuilders.matchQuery("active", true)); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
		searchRequest = searchRequest.source(sourceBuilder);
		
		SearchResponse response = restHighLevelClient.search(searchRequest, new BasicHeader("Content-Type", "application/json"));
		
		logger.info("Status: {}", response.status().getStatus());
		logger.info("total hits {}",response.getHits().getTotalHits());
		List<SearchResult> searchResults = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		for(SearchHit searchHit: response.getHits()){
			searchResults.add(mapper.readValue(searchHit.getSourceAsString(), SearchResult.class));
		}
		
		return searchResults;
	}
}

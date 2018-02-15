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

import elasticsearch.searchscraper.domain.SearchTask;
import elasticsearch.searchscraper.service.api.SearchTaskService;

@Service
public class SearchTaskServiceImpl implements SearchTaskService {

	private static Logger logger = LoggerFactory.getLogger(SearchTaskServiceImpl.class);
	
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Override
	public void save(SearchTask searchTask) throws Exception {
		// Convert the POJO to a JSON string
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(searchTask);

		logger.info("JSON representation of searchtask : {}",json);

		IndexRequest indexRequest = new IndexRequest("searchtasks", "searchtask").source(json,XContentType.JSON);
		restHighLevelClient.index(indexRequest,  new BasicHeader("Content-Type", "application/json"));
	}

	@Override
	public void createIndex() throws Exception {
		String mapping ="{\n" +
				"   \"searchtask\" : {\n" +
				"       \"properties\" : {\n" +
				"            \"taskName\" : {\n" +
				"				\"type\" : \"text\"\n" +
				"			},\n" +
				"            \"keywords\" : { \n" +
				"				\"type\" : \"text\"\n" +
				"			},\n" +
				"            \"createdAt\" : { \n" +
				"				\"type\" : \"date\", \n" +
				"				\"format\": \"epoch_millis||dateOptionalTime\" \n" +
				"			},\n" +
				"            \"active\" : { \n" +
				"				\"type\" : \"boolean\" \n" +
				"			}\n" +
				"		}\n" +
				"	}\n" +
				"}";

		CreateIndexRequest request = new CreateIndexRequest("searchtasks");
		request.mapping("searchtask", mapping, XContentType.JSON);

		restHighLevelClient.indices().create(request, new BasicHeader("Content-Type", "application/json"));
	}

	@Override
	public void deleteIndex() throws Exception {
		DeleteIndexRequest request = new DeleteIndexRequest("searchtasks");
		request.indicesOptions(IndicesOptions.lenientExpandOpen());
		DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(request,
				new BasicHeader("Content-Type", "application/json"));
		logger.info("is ACK {}",deleteIndexResponse.isAcknowledged());
	}

	/**
	 * Since taskName is not unique, there could be more than one search hits.
	 */
	@Override
	public List<SearchTask> findByTaskName(String taskName) throws Exception {
		
		SearchRequest searchRequest = new SearchRequest("searchtasks");
		searchRequest.types("searchtask");
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		sourceBuilder.query(QueryBuilders.matchQuery("taskName", taskName)); 
		sourceBuilder.from(0); 
		sourceBuilder.size(5); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
		searchRequest = searchRequest.source(sourceBuilder);
		
		SearchResponse response = restHighLevelClient.search(searchRequest, new BasicHeader("Content-Type", "application/json"));
		
		logger.info("Status: {}", response.status().getStatus());
		logger.info("total hits {}",response.getHits().getTotalHits());
		
		List<SearchTask> searchTasks = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		for(SearchHit searchHit: response.getHits()){
			searchTasks.add(mapper.readValue(searchHit.getSourceAsString(), SearchTask.class));
		}
		
		return searchTasks;
	}

	@Override
	public List<SearchTask> findActiveSearchTasks() throws Exception {
		SearchRequest searchRequest = new SearchRequest("searchtasks");
		searchRequest.types("searchtask");
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		sourceBuilder.query(QueryBuilders.matchQuery("active", true)); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
		searchRequest = searchRequest.source(sourceBuilder);
		
		SearchResponse response = restHighLevelClient.search(searchRequest, new BasicHeader("Content-Type", "application/json"));
		
		logger.info("Status: {}", response.status().getStatus());
		logger.info("total hits {}",response.getHits().getTotalHits());
		List<SearchTask> searchTasks = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		for(SearchHit searchHit: response.getHits()){
			searchTasks.add(mapper.readValue(searchHit.getSourceAsString(), SearchTask.class));
		}
		
		return searchTasks;
	}
}

package elasticsearch.searchscraper.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import elasticsearch.searchscraper.domain.ScrapeResult;
import elasticsearch.searchscraper.domain.UrlContents;
import elasticsearch.searchscraper.service.api.ScapperService;

@Service
public class ScarpperServiceImpl implements ScapperService {

	private static Logger logger = LoggerFactory.getLogger(ScarpperServiceImpl.class);

	@Override
	public List<ScrapeResult> scrape(String searchWord) throws Exception {
		List<ScrapeResult> scrapeResults = new ArrayList<>();

		final URL url = new URL(
				"https://www.google.com/search?q=" + URLEncoder.encode(searchWord, "UTF-8") + "&num=10&as_qdr=all");
		final URLConnection connection = url.openConnection();

		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);
		connection.addRequestProperty("User-Agent", "Google Chrome/36");

		final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");

		while (reader.hasNextLine()) {
			final String line = reader.nextLine();
			logger.info(line);
			Document doc = Jsoup.parse(line);
			Elements link = doc.select("a[href*=/url]");
			ScrapeResult scrapeResult;
			String urlStr;
			for (Element element : link) {

				if (element != null) {
					logger.info(element.toString());
					// considering only text links.
					// At some point need to consider images also. Need to
					// comeup with title for the image links
					if (element.childNodeSize() == 1 && !"img".equalsIgnoreCase(element.childNode(0).nodeName())) {
						scrapeResult = new ScrapeResult();
						urlStr = element.attr("href");
						scrapeResult.setUrl(urlStr.substring(urlStr.indexOf("=") + 1));
						scrapeResult.setTitle(element.childNode(0).toString());
						scrapeResults.add(scrapeResult);
					}
				}
			}
		}
		reader.close();
		return scrapeResults;
	}

	@Override
	public UrlContents getContents(String urlStr) throws Exception {
		UrlContents urlContents = new UrlContents();
		StringBuilder content = new StringBuilder();

		final URL url = new URL(urlStr);
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);
		connection.addRequestProperty("User-Agent", "Google Chrome/36");
		Scanner reader = null;
		try{
			reader = new Scanner(connection.getInputStream(), "UTF-8");
	
			urlContents.setHttpStatusCode(connection.getResponseCode());
			while (reader.hasNextLine()) {
				final String line = reader.nextLine();
				logger.info(line);
				content.append(line);
			}
			reader.close();
		}catch(IOException e){
			urlContents.setHttpStatusCode(connection.getResponseCode());
		} finally {
			if(null != reader) {
				reader.close();
			}
		}
		
		urlContents.setContent(content.toString());
		return urlContents;
	}
}

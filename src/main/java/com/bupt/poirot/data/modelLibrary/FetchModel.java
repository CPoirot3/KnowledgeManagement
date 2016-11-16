/**
 * Poirot
 * 2016年10月11日上午11:00:30
 * KnowledgeManagement
 */
package com.bupt.poirot.data.modelLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.sparql.pfunction.library.str;

import com.bupt.poirot.z3.parseAndDeduceOWL.OWLToZ3;

/**
 * @author Poirot
 *
 */
public class FetchModel {
	HttpClient httpClient;
	HttpGet httpGet;
	HttpPost httpPost;
	
	public FetchModel() {
		httpClient = HttpClients.createDefault();
		httpGet = new HttpGet("");
		httpPost = new HttpPost();
	}
	
	public boolean modelExist(String urlString, String queryString, String mark) throws UnsupportedEncodingException {
		 
		StringBuilder stringBuilder = new StringBuilder();
		try {
			URI uri = new URI(urlString + URLEncoder.encode(queryString, "utf-8"));
			httpGet.setURI(uri);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (URISyntaxException | UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		if (parse(stringBuilder.toString(), mark)) {
			return true;
		} else {
			return false;
		}
	}
	
	// mark stands for domain
	private boolean parse(String response, String mark) {
		return response.contains(mark);
	}
	
	public InputStream fetch(String host, String domain, String sparqlQuery) {
		InputStream inputStream = null;
		try {
			URI uri = new URI(host + "/" + domain + "?" + URLEncoder.encode(sparqlQuery, "utf-8"));
			httpGet.setURI(uri);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			
		} catch (URISyntaxException | UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
	public static void main(String[] args) {
		FetchModel fetchModel = new FetchModel();
		String host = "http://localhost:3030";
		String domain = "traffic-data";
		//		String query = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object } LIMIT 25";
		String query = "";
		InputStream inputStream = fetchModel.fetch(host, domain, query);
		OWLToZ3.parseFromStream(inputStream);
	}

}

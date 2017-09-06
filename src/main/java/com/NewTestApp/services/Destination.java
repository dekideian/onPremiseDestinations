package com.NewTestApp.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.poi.util.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.DestinationFactory;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.sap.core.connectivity.api.http.HttpDestination;

public class Destination {

	private static final String ON_PREMISE_PROXY = "OnPremise";
	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	private TenantContext tenantContext;

	public String listJNDI() {
		String result = "";
		Context ctx;
		try {
			ctx = Destinations.getContext();
			NamingEnumeration<NameClassPair> list = ctx.list("");
			while (list.hasMore()) {
				result += list.next().getName();
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String getDestinationURL(String destinationName) {
		String url = "";
		try {
			// Look up the connectivity configuration API
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration;
			configuration = (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
			tenantContext = (TenantContext) ctx.lookup("java:comp/env/TenantContext");
			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);
			if (Objects.isNull(destConfiguration)) {
				throw new RuntimeException("Destination %s is not found. Hint:"
						+ " Make sure to have the destination configured." + destinationName);
			}

			// Get the destination URL
			url = destConfiguration.getProperty("URL");

		} catch (NamingException e1) {
			throw new RuntimeException("Connectivity operation failed" + e1);
		}
		return url;
	}

	public String getDestinationWithRestController(String destinationName, String additionalPath) {
		String result = "";
		String fullPath = getDestinationURL(destinationName) + additionalPath;

		ResponseEntity<String> bla = restTemplate().exchange(fullPath, HttpMethod.GET, getHtppHeaders(), String.class);
		result = bla.getBody().toString() + bla.getHeaders().toString();

		return result;
	}

	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		Proxy proxy = getProxy(ON_PREMISE_PROXY);// get this from the configurationdestination if working
		requestFactory.setProxy(proxy);
		return new RestTemplate(requestFactory);
	}

	public HttpEntity<String> getHtppHeaders() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

		// astea sunt necesare la get-ul pt post .. apoi, din header info primit ca
		// raspuns
		// citit X-CSRF-TOKEN, si trimis ca parametru in header la post
		headers.add("X-Requested-With", "XMLHttpRequest");
		headers.add("X-CSRF-Token", "Fetch");

		headers.add("Content-Type", "application/json");
		headers.add("SAP-Connectivity-ConsumerAccount", tenantContext.getTenant().getAccount().getId());

		String auth = "rdurante" + ":" + "Bacardi2019";
		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + encodedAuth;

		headers.add("Authorization", authHeader);
		HttpEntity<String> entity = new HttpEntity<String>("", headers);
		return entity;
	}

	public String getDestinationURLWithConnectivity(String destinationName) {
		String urlPath = "";
		HttpURLConnection urlConnection = null;
		try {
			// Look up the connectivity configuration API
			Context ctx = Destinations.getContext();
			ConnectivityConfiguration configuration;

			configuration = (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
			tenantContext = (TenantContext) ctx.lookup("java:comp/env/TenantContext");

			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);

			if (Objects.isNull(destConfiguration)) {
				throw new RuntimeException("Destination %s is not found. Hint:"
						+ " Make sure to have the destination configured." + destinationName);
			}

			// Get the destination URL
			urlPath = destConfiguration.getProperty("URL");
			URL url = new URL(
					urlPath + "/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ECR_SRV/ProductHierarchySet()?$format=json");

			String proxyType = destConfiguration.getProperty("ProxyType");
			Proxy proxy = getProxy(proxyType);

			urlConnection = (HttpURLConnection) url.openConnection(proxy);

			injectHeader(urlConnection, proxyType);
			InputStream instream = urlConnection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();

		} catch (NamingException e1) {
			throw new RuntimeException(e1);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlPath;
	}

	public HttpResponse getHttpResponse(String path) {
		HttpClient client = null;
		Context ctx;
		try {
			ctx = Destinations.getContext();
			HttpDestination destination = (HttpDestination) ctx.lookup("java:comp/env/http/mdg_destination");
			HttpClient createHttpClient = destination.createHttpClient();
			HttpGet get = new HttpGet(path);
			HttpResponse resp = createHttpClient.execute(get);

			return resp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("bla.." + e);
		}

	}

	public HttpResponse getHttpResponse2(String path) {
		HttpClient client = null;
		Context ctx;
		try {
			ctx = Destinations.getContext();
			HttpDestination destination = null;
			DestinationFactory destinationFactory = (DestinationFactory) ctx.lookup(DestinationFactory.JNDI_NAME);
			destination = (HttpDestination) destinationFactory.getDestination("mdg_destination");
			client = destination.createHttpClient();

			HttpGet get = new HttpGet(path);
			HttpResponse resp = client.execute(get);

			return resp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("bla.." + e);
		}
	}

	private Proxy getProxy(String proxyType) {
		String proxyHost = null;
		int proxyPort;

		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));
		} else {
			// Get proxy for internet destinations
			proxyHost = System.getProperty("https.proxyHost");
			proxyPort = Integer.parseInt(System.getProperty("https.proxyPort"));
		}

		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	}

	private void injectHeader(HttpURLConnection urlConnection, String proxyType) {
		if (ON_PREMISE_PROXY.equals(proxyType)) {
			// Insert header for on-premise connectivity with the consumer
			// account name
			urlConnection.setRequestProperty("SAP-Connectivity-ConsumerAccount",
					tenantContext.getTenant().getAccount().getId());

			String auth = "cdmatos" + ":" + "Bacardi2017";
			String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + encodedAuth;

			urlConnection.setRequestProperty("Authorization", authHeader);
		}
	}

	private void copyStream(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
	}

	public String getDestinationWithRestController2(String destinationName, String string2) {
		String url = "";
		try {
			// Look up the connectivity configuration API
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration;
			configuration = (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
			tenantContext = (TenantContext) ctx.lookup("java:comp/env/TenantContext");
			// Get destination configuration for "destinationName"
			DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);
			return destConfiguration.getKeyStore().toString();
			// if (Objects.isNull(destConfiguration)) {
			// throw new RuntimeException("Destination %s is not found. Hint:"
			// + " Make sure to have the destination configured." + destinationName);
			// }

			// Get the destination URL
			// url = destConfiguration.getProperty("URL");

		} catch (NamingException e1) {
			throw new RuntimeException("Connectivity operation failed" + e1);
		}
		// return url;
	}

	public String postDestinationWithRestController(String destinationName, String additionalPath) {
		String result = "";
		String fullPath = getDestinationURL(destinationName) + additionalPath;

		ResponseEntity<String> bla = restTemplate().exchange(fullPath, HttpMethod.GET, getHtppHeaders(), String.class);
			String token = bla.getHeaders().get("X-CSRF-Token").get(0);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

			// astea sunt necesare la get-ul pt post .. apoi, din header info primit ca
			// raspuns
			// citit X-CSRF-TOKEN, si trimis ca parametru in header la post
			headers.add("X-Requested-With", "XMLHttpRequest");
			headers.add("X-CSRF-Token", token);

			headers.add("Content-Type", "application/json");
			headers.add("SAP-Connectivity-ConsumerAccount", tenantContext.getTenant().getAccount().getId());

			String auth = "rdurante" + ":" + "Bacardi2019";
			String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + encodedAuth;

			headers.add("Authorization", authHeader);
			HttpEntity<String> entity = new HttpEntity<String>("", headers);
		
			ResponseEntity<String> bla2 = restTemplate().exchange(fullPath, HttpMethod.GET, entity, String.class);
			result = bla2.getBody()+"--------"+bla2.getHeaders();
		return result;
	}
}

package com.NewTestApp.services;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class LeServ {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String uri = "https://webide-a2ddb4565.dispatcher.hana.ondemand.com/destinations/mdg_destination/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ECR_SRV/HeaderSet";
	// private final String uri2 =
	// "https://mdgsimulatorapi0i336177trial.hanatrial.ondemand.com/mdg-simulator-api-0.0.1-SNAPSHOT/productHierarchies";

	public String giveMeSmg() {
		String result = getHeaderInformation().getHeaders().toString();
		result += "\n " + getHeaderInformation().getBody().toString();
		return result;
	}

	public String giveMeJSON() {
		return "{\"menu\": {\"id\": \"file\",\"value\": \"File\",\"popup\": {\"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}]}}}";
	}

	private ResponseEntity<String> getHeaderInformation() {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("X-Requested-With", "XMLHttpRequest");
		headers.add("Accept", "application/atom+xml,application/atomsvc+xml,application/xml");
		headers.add("Content-Type", "application/atom+xml");
		headers.add("DataServiceVersion", "2.0");
		headers.add("X-CSRF-Token", "Fetch");
		headers.add("Content-Type", "application/json");

		String auth = "cdmatos" + ":" + "Bacardi2017";
		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + encodedAuth;

		headers.add("Authorization", authHeader);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		HttpEntity<String> entity = new HttpEntity<String>("", headers);

		ResponseEntity<String> bla = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		return bla;
	}

	public String getDestinationName() {
		String result = "";
		String destinationName = "dest_bla";

		// HttpDestination destination = null;
		// DestinationFactory destinationFactory;
		// try {
		// Context context = new InitialContext();
		// destinationFactory = (DestinationFactory)
		// context.lookup(DestinationFactory.JNDI_NAME);
		// destination = (HttpDestination)
		// destinationFactory.getDestination(destinationName);
		// result = destination.getName()+"->"+destination.getURI().toString();
		// } catch (NamingException e) {
		// e.printStackTrace();
		// throw new RuntimeException(e);
		// } catch (DestinationNotFoundException e) {
		// e.printStackTrace();
		// throw new RuntimeException(e);
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// throw new RuntimeException(e);
		// }

		return result;
	}

	public String giveDestination(String destinationName) {
		return (new Destination()).getDestinationURL(destinationName);
	}

	public String getHttpResponeProductHierarchySet() {
		return (new Destination()).getHttpResponse("/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_INT_SRV/ProductHierarchySet")
				.toString();
	}

	public String getHttpResponeProductHierarchySetTrial() {
		return (new Destination()).getHttpResponse("imghp").toString();
	}

	public String getHttpResponeProductHierarchySetTrial2() {
		return (new Destination()).getHttpResponse2("imghp").toString();
	}

	public String listContext() {
		return (new Destination()).listJNDI();
	}

	public String getDestinationWithRestController(String string, String string2) {
		return (new Destination()).getDestinationWithRestController(string, string2);
	}

	public String getDestinationWithRestController2(String string, String string2) {
		return (new Destination()).getDestinationWithRestController2(string, string2);
	}

	public String postDestinationWithRestController(String string, String string2) {
		return (new Destination()).postDestinationWithRestController(string, string2);
	}
}

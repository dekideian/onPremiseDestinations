package com.NewTestApp.controllers;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.NewTestApp.services.LeServ;

@RestController
public class HomePageController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LeServ leServ;

	@GetMapping
	public String homePage() {
		return "it Works!";
	}

	@GetMapping("/dest")
	public String bla3() {
		return leServ.giveDestination("mdg_destination");
	}

	@GetMapping("/productHierarchy")
	public String bla33() {
		return leServ.getDestinationWithRestController("mdg_destination",
				"/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_INT_SRV/ProductHierarchySet()?$format=json");
	}

	@GetMapping("/getHeaderSet")
	public String bla31() {
		return leServ.getDestinationWithRestController("mdg_destination",
				"/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ecr_SRV/HeaderSet()");
	}
	
	@GetMapping("/postHeaderSet")
	public String please() {
		return leServ.postDestinationWithRestController("mdg_destination",
				"/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ecr_SRV/HeaderSet()");
	}
//
//	@GetMapping("/rest2")
//	public String bla32() {
//		return leServ.getDestinationWithRestController("mdg_destination",
//				"/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ecr_SRV/HeaderSet");
//	}
//
//	@GetMapping("/dest2")
//	public String bla41() {
//		String url = leServ.giveDestination("mdg_destination") + "/productHierarchies";
//		String result = restTemplate.getForObject(url, String.class);
//		return result;
//	}

//	@GetMapping("/hierarchies")
//	public String bla5() {
//		// use destination, rest template for destination + link..
//		// /sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ECR_SRV/$metadata
//		String baseDestination = leServ.giveDestination("mdg_destination");
//		String restDestination = "/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ECR_SRV/$metadata";
//
//		String result = restTemplate.getForObject(baseDestination + restDestination, String.class).toString();
//		return result;
//	}

//	@GetMapping("/hierarchies1")
//	public String caca() {
//		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
//
//		headers.add("X-Requested-With", "XMLHttpRequest");
//		headers.add("Accept", "application/atom+xml,application/atomsvc+xml,application/xml");
//		headers.add("Content-Type", "application/atom+xml");
//		headers.add("DataServiceVersion", "2.0");
//		// headers.add("X-CSRF-Token", "Fetch");
//		headers.add("Content-Type", "application/json");
//
//		String auth = "cdmatos" + ":" + "Bacardi2017";
//		String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("US-ASCII")));
//		String authHeader = "Basic " + encodedAuth;
//
//		headers.add("Authorization", authHeader);
//		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//		HttpEntity<String> entity = new HttpEntity<String>("", headers);
//
//		ResponseEntity<String> bla = restTemplate.exchange(
//				"https://webide-a2ddb4565.dispatcher.hana.ondemand.com/destinations/mdg_destination/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_INT_SRV/ProductHierarchySet",
//				HttpMethod.GET, entity, String.class);
//		return bla.toString();
//	}

//	@GetMapping("/hierarchies2")
//	public String bla6() {
//		// use destination, rest template for destination + link..
//		// /sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_ECR_SRV/$metadata
//		String uri = "https://webide-a2ddb4565.dispatcher.hana.ondemand.com/destinations/mdg_destination/sap/opu/odata/SAP/ZMDG_SCP_INNOVAPP_INT_SRV/ProductHierarchySet";
//		String result = restTemplate.getForObject(uri, String.class).toString();
//		return result;
//	}
//
//	@GetMapping("/hierarchies3")
//	public String bla7() {
//		return leServ.getHttpResponeProductHierarchySet();
//	}
//
//	// listContext
//	@GetMapping("/listJNDI")
//	public String bla71() {
//		return leServ.listContext();
//	}
//
//	@GetMapping("/hierarchies4")
//	public String bla8() {
//		return leServ.getHttpResponeProductHierarchySetTrial();
//	}
//
//	@GetMapping("/hierarchies5")
//	public String bla9() {
//		return leServ.getHttpResponeProductHierarchySetTrial2();
//	}

	private ResponseEntity<String> getResponse(String baseDestination, String restDestination) {
		return restTemplate.getForEntity(baseDestination + restDestination, String.class);
	}
}

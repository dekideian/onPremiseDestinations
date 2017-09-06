package com.NewTestApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import com.NewTestApp.services.Destination;
import com.NewTestApp.services.LeServ;

@Configuration
@Import(SpringSecurityConfig.class)
public class AppConfig {

    @Bean public LeServ getLeServ(){
	return new LeServ();
    }
//    @Bean public Destination getDestination(){
//	return new Destination();
//    }
    
    @Bean public RestTemplate getRestTemplate(){
	return new RestTemplate();
    }
    
//    @Bean public DestinationFactory getDestination
}

package com.kbytes.safetynet;

import javax.annotation.PostConstruct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbytes.safetynet.repository.SafetynetDataSource;

@Component
public class FireStationDataStore implements CommandLineRunner {
	private SafetynetDataSource dataSource;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read JSON file and convert to a customer object
        dataSource = objectMapper.readValue(new ClassPathResource("./data/data.json").getFile(), SafetynetDataSource.class);

        //print customer details
        System.out.println(dataSource);		
	}

	public SafetynetDataSource getDataSource() {
		return dataSource;
	}
}

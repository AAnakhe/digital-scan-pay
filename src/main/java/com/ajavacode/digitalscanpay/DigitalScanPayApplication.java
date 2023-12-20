package com.ajavacode.digitalscanpay;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.json.jackson.DatabindCodec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DigitalScanPayApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DigitalScanPayApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(DigitalScanPayApplication.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.setAdditionalProfiles("prod");
		springApplication.run(args);

		ObjectMapper objectMapper = DatabindCodec.mapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
}

package com.shooraglobal.agent_database_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class AgentDatabaseServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(AgentDatabaseServiceApplication.class);

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		log.info("Starting agent database service");

		ConfigurableApplicationContext context = SpringApplication.run(AgentDatabaseServiceApplication.class, args);
		Environment environment = context.getEnvironment();
		String applicationName = environment.getProperty("spring.application.name", "agent_database_service");
		String serverPort = environment.getProperty("server.port", "8080");
		String activeProfiles = Arrays.toString(environment.getActiveProfiles());
		long startupTime = System.currentTimeMillis() - startTime;

		log.info(
				"Started {} on port {} with active profiles {} in {} ms",
				applicationName,
				serverPort,
				activeProfiles,
				startupTime
		);
	}

}

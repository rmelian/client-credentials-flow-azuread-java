package com.client.clientapplication;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Value("${clientId}")
	String clientId;

	@Value("${clientSecret}")
	String clientSecret;

	@Override
	public void run(String... args) throws Exception {
		String authority = "https://login.microsoftonline.com/common";
		ExecutorService service = Executors.newFixedThreadPool(1);

		AuthenticationContext context = new AuthenticationContext(authority, true, service);

		String resource = "https://raiselmeliangmail.onmicrosoft.com/ResourceWebAPI";

		Future<AuthenticationResult> future = context.acquireToken(resource, new ClientCredential(clientId, clientSecret), null);
		AuthenticationResult result = future.get();

		System.out.println("Bearer " + result.getAccessToken());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer " + result.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		restTemplate.exchange("http://localhost:8082/api", HttpMethod.POST, entity, String.class);

	}
}

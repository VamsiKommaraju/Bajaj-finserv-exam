package com.example.bajajFinserv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class VamsiKrishna22BIT0344Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(VamsiKrishna22BIT0344Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        // 1️⃣ Generate Webhook
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Vamsi Krishna Mohan");
        requestBody.put("regNo", "0344");
        requestBody.put("email", "vamsikommaraju@gmail.com"); // change if needed

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, request, Map.class);

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // 2️⃣ Build SQL Query for Question 2 (younger employees count per department)
        String finalQuery = """
                SELECT 
                    e.EMP_ID,
                    e.FIRST_NAME,
                    e.LAST_NAME,
                    d.DEPARTMENT_NAME,
                    COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
                FROM EMPLOYEE e
                JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
                LEFT JOIN EMPLOYEE e2 ON e.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e.DOB
                GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME
                ORDER BY e.EMP_ID DESC;
                """;

        // 3️⃣ Send final SQL query to webhook
        HttpHeaders webhookHeaders = new HttpHeaders();
        webhookHeaders.setContentType(MediaType.APPLICATION_JSON);
        webhookHeaders.set("Authorization", accessToken); // exact token

        Map<String, String> finalBody = new HashMap<>();
        finalBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> webhookRequest = new HttpEntity<>(finalBody, webhookHeaders);

        ResponseEntity<String> webhookResponse = restTemplate.postForEntity(webhookUrl, webhookRequest, String.class);

        System.out.println("Webhook Response: " + webhookResponse.getStatusCode());
        System.out.println("Response Body: " + webhookResponse.getBody());
    }
}

package com.email.writer.email.writer.app;

import java.util.*;

import com.email.writer.email.writer.app.EmailRequest;
import com.email.writer.email.writer.app.EmailHistory;
import com.email.writer.email.writer.app.EmailHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    // ADDED
    private final EmailHistoryRepository repository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(
            WebClient.Builder webClientBuilder,
            EmailHistoryRepository repository) {

        this.webClient = webClientBuilder.build();

        // ADDED
        this.repository = repository;
    }

    public String generateEmailReply(EmailRequest emailRequest) {

        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Craft a request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        // Do request and get response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);

        // Extract Response
        String generatedReply = extractResponseContent(response);

        // ADDED DATABASE SAVE
        EmailHistory history = new EmailHistory();

        history.setEmailContent(emailRequest.getEmailContent());
        history.setTone(emailRequest.getTone());
        history.setGeneratedReply(generatedReply);

        repository.save(history);

        // Return Response
        return generatedReply;
    }

    private String extractResponseContent(String response) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(response);

            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {

            return "Error Processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {

        StringBuilder prompt = new StringBuilder();

        prompt.append(
                "Generate a professional email reply for the following email content. Please don't generate a subject line"
        );

        if (emailRequest.getTone() != null &&
                !emailRequest.getTone().isEmpty()) {

            prompt.append("Use a ")
                    .append(emailRequest.getTone())
                    .append(" tone. ");
        }

        prompt.append("\nOriginal email: \n")
                .append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}

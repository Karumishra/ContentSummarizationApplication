package com.summarization.contentsummarization.service;

import com.summarization.contentsummarization.dto.SummarizationResponseDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummarizationServiceImpl implements SummarizationService {

    private static final Logger logger = LoggerFactory.getLogger(SummarizationServiceImpl.class);
    @Value("${openai.api.key}")
    private String openAPIKey;
    @Value("${openai.api.url}")
    private String openAPIUrl;
    @Autowired
    private RestTemplate restTemplate;

    public SummarizationResponseDTO summarizeContent(MultipartFile inputFile) {

        logger.info("Starting content summarization");

        // Converting inputFile to String
        String Text = extractTextFromMultipartFile(inputFile);

        // Setting HTTPHeaders
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Authorization", "Bearer " + openAPIKey);

        JSONObject jsonObject = new JSONObject();

        //Storing role and content to Map List message
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", Text);
        messages.add(message);

        //Assigning model and message to requestBody
        jsonObject.put("model","gpt-3.5-turbo-16k");
        jsonObject.put("messages", messages);
        jsonObject.put("max_tokens", 100);
        String requestBody = jsonObject.toString();

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        logger.info("Starting communication with Open AI API");
        // Calling open ai api through rest template
        ResponseEntity<String> response = restTemplate.postForEntity(openAPIUrl, httpEntity, String.class);

        logger.info("Completing communication with Open AI API");

        return getFilteredResponse(response.getBody());

    }

    // Utility Method to convert File to String
    public String extractTextFromMultipartFile(MultipartFile multipartFile) {
        logger.info("Starting extracting Text from File");
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            // Handle the exception appropriately, e.g., logging or throwing a custom exception
            e.printStackTrace();
        }
        logger.info("Completing extracting Text from File");
        return content.toString();
    }

    //Utlity Method for filtering content string from return json from Open AI
    public SummarizationResponseDTO getFilteredResponse(String jsonData)
    {
        logger.info("Starting filtering required content");
        JSONObject jsonDat = new JSONObject(jsonData);
        JSONArray choices = jsonDat.getJSONArray("choices");

        JSONObject choice = choices.getJSONObject(0);
        JSONObject mes = choice.getJSONObject("message");
        String content = mes.getString("content").replace("\n","")
                .replace("/","");
        logger.info("Completing filtering required content");
        logger.info("Completing content summarization task");
        return new SummarizationResponseDTO(content);
    }


}

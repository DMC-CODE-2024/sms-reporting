package com.ceir.SmsCallbackProcess.service;


import com.ceir.SmsCallbackProcess.enums.DeliveryStatus;
import com.ceir.SmsCallbackProcess.model.Notification;
import com.ceir.SmsCallbackProcess.model.SystemConfigurationDb;
import com.ceir.SmsCallbackProcess.repository.NotificationRepository;
import com.ceir.SmsCallbackProcess.repository.impl.SystemConfigurationDbRepoImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CallbackProcessor implements Runnable {
    private final Logger log = LogManager.getLogger(getClass());
    @Value("${mekongnet-sync-duration-in-days:3}")
    private Integer durationOfDays;
    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy").withLocale(java.util.Locale.ENGLISH);
        LocalDate now = LocalDate.now();
        String endDate = now.format(formatter).toLowerCase();
        String startDate = now.minusDays(durationOfDays).format(formatter).toLowerCase();
        SystemConfigurationDb aggReportUrl = systemConfigRepoImpl.getDataByTag("agg_report_url");
        SystemConfigurationDb aggUsername = systemConfigRepoImpl.getDataByTag("agg_username");
        SystemConfigurationDb aggPassword = systemConfigRepoImpl.getDataByTag("agg_password");
        try {
            Map<String, String> idStatusMap = fetchDataFromAPI(aggReportUrl.getValue(), aggUsername.getValue(), aggPassword.getValue(), startDate, endDate);
            if (idStatusMap.size() > 0) {
                for (Map.Entry<String, String> entry : idStatusMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    Notification noti = notificationRepository.findByCorelationId(key);

                    if (noti != null) {
                        noti.setDeliveryTime(LocalDateTime.now());
                        DeliveryStatus status = DeliveryStatus.valueOf(value.toUpperCase().replace(" ", "_"));
                        noti.setDeliveryStatus(status.getValue());
                        notificationRepository.save(noti);
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public Map<String, String> fetchDataFromAPI(String url, String username, String password, String sd, String ed) throws URISyntaxException {
        Map<String, String> dataMap = new HashMap<>();

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("username", username);
            uriBuilder.setParameter("pass", password);
            uriBuilder.setParameter("isj", "1");
            uriBuilder.setParameter("sd", sd);
            uriBuilder.setParameter("ed", ed);
            URI uri = uriBuilder.build();

            // Make the API call and get the JSON response
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Final Request URL: " + uri.toString());
            System.out.println("Response from the API: "+response.body());

            if (response.body() != null && !response.body().isEmpty()) {
                // Parse the JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.body());

                // Extract CustomData and Status from each JSON object
                for (JsonNode node : jsonNode) {
                    String customData = node.get("CustomData").asText();
                    String status = node.get("Status").asText().toUpperCase();

                    dataMap.put(customData, status);
                }
            } else {
                log.info("Response body is empty or null for sd: {}, ed: {}",sd, ed);
                System.out.println("Response body is empty or null");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return dataMap;
    }
}

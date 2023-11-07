package com.ceir.SmsCallbackProcess.service;


import com.ceir.SmsCallbackProcess.enums.DeliveryStatus;
import com.ceir.SmsCallbackProcess.model.Notification;
import com.ceir.SmsCallbackProcess.model.SystemConfigurationDb;
import com.ceir.SmsCallbackProcess.repository.NotificationRepository;
import com.ceir.SmsCallbackProcess.repository.impl.SystemConfigurationDbRepoImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    SystemConfigurationDbRepoImpl systemConfigRepoImpl;
    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy").withLocale(java.util.Locale.ENGLISH);
        String lastRunDate;
        String startDate;
        Optional<SystemConfigurationDb> lastRunTimeOp = Optional.ofNullable(systemConfigRepoImpl.getDataByTag("agg_report_last_run_time"));
        if(!lastRunTimeOp.isPresent()) {
            lastRunDate = LocalDate.now().minusDays(1).format(formatter).toLowerCase();
            startDate = LocalDate.now().format(formatter).toLowerCase();
        } else {
            lastRunDate = lastRunTimeOp.get().getValue();
            LocalDate date = LocalDate.parse(lastRunDate, formatter);
            LocalDate nextDate = date.plusDays(1);
            startDate = nextDate.format(formatter).toLowerCase();
        }
        SystemConfigurationDb aggReportUrl = systemConfigRepoImpl.getDataByTag("agg_report_url");
        SystemConfigurationDb aggUsername = systemConfigRepoImpl.getDataByTag("agg_username");
        SystemConfigurationDb aggPassword = systemConfigRepoImpl.getDataByTag("agg_password");
        try {
            Map<String, String> idStatusMap = fetchDataFromAPI(aggReportUrl.getValue(), aggUsername.getValue(), aggPassword.getValue(), startDate, lastRunDate);
            if (idStatusMap.size() > 0) {
                for (Map.Entry<String, String> entry : idStatusMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    Notification noti = notificationRepository.findByCorelationIdAndOperatorName(key, value);

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

    public static Map<String, String> fetchDataFromAPI(String url, String username, String password, String sd, String ed) throws URISyntaxException {
        Map<String, String> dataMap = new HashMap<>();

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("username", username);
            uriBuilder.setParameter("pass", password);
            uriBuilder.setParameter("isj", 1);
            uriBuilder.setParameter("sd", sd);
            uriBuilder.setParameter("ed", ed);
            URI uri = uriBuilder.build();

            // Make the API call and get the JSON response
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());

            // Extract CustomData and Status from each JSON object
            for (JsonNode node : jsonNode) {
                String customData = node.get("CustomData").asText();
                String status = node.get("Status").asText();

                dataMap.put(customData, status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return dataMap;
    }
}

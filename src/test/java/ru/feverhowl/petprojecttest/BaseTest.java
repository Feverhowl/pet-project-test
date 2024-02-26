package ru.feverhowl.petprojecttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.SkipException;
import ru.feverhowl.petprojecttest.repos.PetRepository;
import ru.feverhowl.tools.data.context.TDMTestCaseContext;

import java.io.IOException;
import java.util.*;

/**
 * @author Dmitrii Zolotarev
 */
@Slf4j
@Service
public class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired protected PetRepository petRepository;
    protected RestTemplate restTemplate;
    protected ResponseEntity<String> response;
    protected String responseBody;

    protected static final String BASE_URL = "http://localhost:8888/pet-project/pets";
    protected static final String ALLURE_RESULTS = "allure-results";
    protected static final String ALLURE_RESULTS_HISTORY = "allure-results/history";
    protected static final String ALLURE_REPORT = "allure-report";
    protected static final String ALLURE_REPORT_HISTORY = "allure-report/history";

    protected void configureTestCase(TDMTestCaseContext context) {
        String description = context.getConfiguration().getDescription();
        Allure.getLifecycle().updateTestCase(
                update -> update
                        .setDescription(description)
                        .setName(description)
                        .getParameters().clear()
        );
        log.info("Test-case => " + getRunningTestMethod() + ": " + description);

        Optional.ofNullable(context.getConfiguration().getParameters())
                .orElse(new HashMap<>())
                .computeIfPresent("SKIP_REASON", (k, reason) -> {
                    log.warn("Test was skipped - " + reason);
                    throw new SkipException("Test was skipped - " + reason);
                });
    }

    protected static String getRunningTestMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    protected ResponseEntity<String> getResponse(String URI) {
        response = restTemplate.getForEntity(URI, String.class);
        responseBody = response.getBody();
        return response;
    }

    protected ResponseEntity<String> getResponse(String URI, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        response = restTemplate.postForEntity(URI, entity, String.class);
        responseBody = response.getBody();
        return response;
    }

    protected <T> T getObjectFromResponseBody(String responseBody, Class<T> clazz) throws IOException {
        if (responseBody == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("CET")); // Asia/Tbilisi
        return mapper.readValue(responseBody, clazz);
    }
    protected <T> List<T> getObjectsFromResponseBody(String responseBody, Class<T> clazz) throws IOException {
        if (responseBody == null) return new ArrayList<>();
        List<T> result = new ArrayList<>();
        String[] array = responseBody.substring(1, responseBody.length() - 1).split("},");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("CET")); // Asia/Tbilisi
        for (String s : array) result.add(mapper.readValue((array.length > 1 ? "" : "{") + s + "}", clazz));
        return result;
    }
}

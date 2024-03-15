package com.example.circuit.controller;

import com.example.circuit.entity.Activity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequestMapping("/activity")
@RestController
public class ActivityController {

    private final RestTemplate restTemplate;

    private final String BORED_API = "https://www.boredapi.com/api/activity";

    public ActivityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    @CircuitBreaker(name = "randomActivity", fallbackMethod = "fallbackRandomActivity")
    public String getRandomActivity() {
        try {
            ResponseEntity<Activity> responseEntity = restTemplate.getForEntity(BORED_API, Activity.class);
            Activity activity = responseEntity.getBody();
            assert activity != null;
            log.info("Activity received: {}", activity.getActivity());
            return activity.getActivity();
        } catch (Exception e) {
            log.error("Error occurred while fetching random activity: {}", e.getMessage());
            throw e; }
    }

    public String fallbackRandomActivity(Throwable throwable) {
        log.warn("Circuit breaker triggered. Falling back to default activity.");
        return "Circuit Breaker work!";
    }

}

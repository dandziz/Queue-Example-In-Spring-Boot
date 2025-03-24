package com.dandziz.bookhub.controllers;

import com.dandziz.bookhub.services.TestHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class TestController {

    private final TestHelperService testHelperService;

    @GetMapping
    public String getHome() throws InterruptedException {
        testHelperService.testAsync();
        return "Hello";
    }

    @GetMapping(path = "/v2")
    public String getHomeV2() throws InterruptedException {
        return "Good Luck";
    }

    @GetMapping(path = "/order")
    public String getOrder() throws InterruptedException {
        testHelperService.testOrderAsync();
        return "Test Order";
    }

    @GetMapping(path = "/notification")
    public String getNotification() throws InterruptedException {
        testHelperService.testNotificationAsync();
        return "Test Notification";
    }
}

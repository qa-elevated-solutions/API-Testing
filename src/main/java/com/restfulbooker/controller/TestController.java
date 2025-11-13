package com.restfulbooker.controller;

import com.restfulbooker.service.ApiTestService;
import com.restfulbooker.model.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private ApiTestService testService;

    @GetMapping("/health")
    public TestResult runHealthCheck() {
        return testService.testHealthCheck();
    }

    @GetMapping("/create-booking")
    public TestResult runCreateBooking() {
        return testService.testCreateBooking();
    }

    @GetMapping("/get-booking")
    public TestResult runGetBooking() {
        return testService.testGetBooking();
    }

    @GetMapping("/get-all-bookings")
    public TestResult runGetAllBookings() {
        return testService.testGetAllBookings();
    }

    @GetMapping("/get-bookings-by-name")
    public TestResult runGetBookingsByName() {
        return testService.testGetBookingsByName();
    }

    @GetMapping("/update-booking")
    public TestResult runUpdateBooking() {
        return testService.testUpdateBooking();
    }

    @GetMapping("/partial-update")
    public TestResult runPartialUpdate() {
        return testService.testPartialUpdate();
    }

    @GetMapping("/delete-booking")
    public TestResult runDeleteBooking() {
        return testService.testDeleteBooking();
    }

    @GetMapping("/invalid-data")
    public TestResult runInvalidData() {
        return testService.testInvalidData();
    }

    @GetMapping("/non-existent")
    public TestResult runNonExistent() {
        return testService.testNonExistent();
    }
}
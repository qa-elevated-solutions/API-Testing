package com.restfulbooker.model;

public class TestResult {
    private String testName;
    private String status;
    private String message;
    private int statusCode;
    private String responseBody;
    private long duration;

    // Getters and Setters
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}
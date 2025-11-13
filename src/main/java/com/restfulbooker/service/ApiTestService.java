package com.restfulbooker.service;

import com.restfulbooker.model.TestResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import static io.restassured.RestAssured.*;

@Service
public class ApiTestService {

    private static String baseUrl = "https://restful-booker.herokuapp.com";
    private static String authToken;
    private static Integer bookingId;

    public ApiTestService() {
        RestAssured.baseURI = baseUrl;
        initializeAuth();
    }

    private void initializeAuth() {
        try {
            authToken = given()
                    .contentType(ContentType.JSON)
                    .body("{\n" +
                            "    \"username\": \"admin\",\n" +
                            "    \"password\": \"password123\"\n" +
                            "}")
                    .when()
                    .post("/auth")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("token");
        } catch (Exception e) {
            System.err.println("Failed to get auth token: " + e.getMessage());
        }
    }

    public TestResult testHealthCheck() {
        TestResult result = new TestResult();
        result.setTestName("Health Check");
        long startTime = System.currentTimeMillis();

        try {
            Response response = given()
                    .when()
                    .get("/ping");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 201) {
                result.setStatus("PASSED");
                result.setMessage("API is healthy and responding");
            } else {
                result.setStatus("FAILED");
                result.setMessage("Unexpected status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testCreateBooking() {
        TestResult result = new TestResult();
        result.setTestName("Create Booking");
        long startTime = System.currentTimeMillis();

        try {
            String requestBody = "{\n" +
                    "    \"firstname\": \"John\",\n" +
                    "    \"lastname\": \"Doe\",\n" +
                    "    \"totalprice\": 150,\n" +
                    "    \"depositpaid\": true,\n" +
                    "    \"bookingdates\": {\n" +
                    "        \"checkin\": \"2024-12-01\",\n" +
                    "        \"checkout\": \"2024-12-10\"\n" +
                    "    },\n" +
                    "    \"additionalneeds\": \"Breakfast\"\n" +
                    "}";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/booking");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 200) {
                bookingId = response.path("bookingid");
                result.setStatus("PASSED");
                result.setMessage("Booking created successfully with ID: " + bookingId);
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to create booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testGetBooking() {
        TestResult result = new TestResult();
        result.setTestName("Get Booking by ID");
        long startTime = System.currentTimeMillis();

        try {
            if (bookingId == null) {
                testCreateBooking();
            }

            Response response = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/booking/" + bookingId);

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 200) {
                result.setStatus("PASSED");
                result.setMessage("Successfully retrieved booking ID: " + bookingId);
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to retrieve booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testGetAllBookings() {
        TestResult result = new TestResult();
        result.setTestName("Get All Bookings");
        long startTime = System.currentTimeMillis();

        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/booking");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());

            if (response.getStatusCode() == 200) {
                int count = response.jsonPath().getList("$").size();
                result.setStatus("PASSED");
                result.setMessage("Retrieved " + count + " bookings");
                result.setResponseBody("Total bookings: " + count);
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to retrieve bookings");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testGetBookingsByName() {
        TestResult result = new TestResult();
        result.setTestName("Get Bookings by Name");
        long startTime = System.currentTimeMillis();

        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .queryParam("firstname", "John")
                    .queryParam("lastname", "Doe")
                    .when()
                    .get("/booking");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());

            if (response.getStatusCode() == 200) {
                int count = response.jsonPath().getList("$").size();
                result.setStatus("PASSED");
                result.setMessage("Found " + count + " bookings matching John Doe");
                result.setResponseBody("Matching bookings: " + count);
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to filter bookings");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testUpdateBooking() {
        TestResult result = new TestResult();
        result.setTestName("Update Booking (PUT)");
        long startTime = System.currentTimeMillis();

        try {
            if (bookingId == null) {
                testCreateBooking();
            }

            String updateBody = "{\n" +
                    "    \"firstname\": \"Jane\",\n" +
                    "    \"lastname\": \"Smith\",\n" +
                    "    \"totalprice\": 200,\n" +
                    "    \"depositpaid\": false,\n" +
                    "    \"bookingdates\": {\n" +
                    "        \"checkin\": \"2024-12-15\",\n" +
                    "        \"checkout\": \"2024-12-20\"\n" +
                    "    },\n" +
                    "    \"additionalneeds\": \"Lunch\"\n" +
                    "}";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("Cookie", "token=" + authToken)
                    .body(updateBody)
                    .when()
                    .put("/booking/" + bookingId);

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 200) {
                result.setStatus("PASSED");
                result.setMessage("Booking updated successfully to Jane Smith");
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to update booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testPartialUpdate() {
        TestResult result = new TestResult();
        result.setTestName("Partial Update (PATCH)");
        long startTime = System.currentTimeMillis();

        try {
            if (bookingId == null) {
                testCreateBooking();
            }

            String partialUpdate = "{\n" +
                    "    \"firstname\": \"Michael\",\n" +
                    "    \"lastname\": \"Johnson\"\n" +
                    "}";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("Cookie", "token=" + authToken)
                    .body(partialUpdate)
                    .when()
                    .patch("/booking/" + bookingId);

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 200) {
                result.setStatus("PASSED");
                result.setMessage("Partial update successful - name changed to Michael Johnson");
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to partially update booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testDeleteBooking() {
        TestResult result = new TestResult();
        result.setTestName("Delete Booking");
        long startTime = System.currentTimeMillis();

        try {
            if (bookingId == null) {
                testCreateBooking();
            }

            int idToDelete = bookingId;

            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("Cookie", "token=" + authToken)
                    .when()
                    .delete("/booking/" + idToDelete);

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());

            if (response.getStatusCode() == 201) {
                result.setStatus("PASSED");
                result.setMessage("Booking " + idToDelete + " deleted successfully");
                result.setResponseBody("Deleted");
                bookingId = null;
            } else {
                result.setStatus("FAILED");
                result.setMessage("Failed to delete booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testInvalidData() {
        TestResult result = new TestResult();
        result.setTestName("Create Booking with Invalid Data");
        long startTime = System.currentTimeMillis();

        try {
            String invalidBody = "{\n" +
                    "    \"firstname\": \"Test\"\n" +
                    "}";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(invalidBody)
                    .when()
                    .post("/booking");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 500) {
                result.setStatus("PASSED");
                result.setMessage("API correctly rejected invalid data with 500 error");
            } else {
                result.setStatus("FAILED");
                result.setMessage("Expected 500 error for invalid data");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    public TestResult testNonExistent() {
        TestResult result = new TestResult();
        result.setTestName("Get Non-Existent Booking");
        long startTime = System.currentTimeMillis();

        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/booking/999999");

            long duration = System.currentTimeMillis() - startTime;
            result.setDuration(duration);
            result.setStatusCode(response.getStatusCode());
            result.setResponseBody(response.getBody().asString());

            if (response.getStatusCode() == 404) {
                result.setStatus("PASSED");
                result.setMessage("API correctly returned 404 for non-existent booking");
            } else {
                result.setStatus("FAILED");
                result.setMessage("Expected 404 for non-existent booking");
            }
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.setMessage("Exception: " + e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
        }

        return result;
    }
}
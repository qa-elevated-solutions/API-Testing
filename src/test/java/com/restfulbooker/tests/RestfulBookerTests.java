package com.restfulbooker.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestfulBookerTests {

    private static String baseUrl = "https://restful-booker.herokuapp.com";
    private static String authToken;
    private static int bookingId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = baseUrl;

        // Get authentication token
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

        System.out.println("Auth Token: " + authToken);
    }

    @Test
    @Order(1)
    @DisplayName("Create a new booking")
    public void testCreateBooking() {
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
                .post("/booking")
                .then()
                .statusCode(200)
                .body("booking.firstname", equalTo("John"))
                .body("booking.lastname", equalTo("Doe"))
                .body("booking.totalprice", equalTo(150))
                .body("booking.depositpaid", equalTo(true))
                .extract()
                .response();

        bookingId = response.path("bookingid");
        System.out.println("Created Booking ID: " + bookingId);
    }

    @Test
    @Order(2)
    @DisplayName("Get booking by ID")
    public void testGetBooking() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("totalprice", equalTo(150))
                .body("bookingdates.checkin", equalTo("2024-12-01"))
                .body("bookingdates.checkout", equalTo("2024-12-10"));
    }

    @Test
    @Order(3)
    @DisplayName("Get all bookings")
    public void testGetAllBookings() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("bookingid", everyItem(notNullValue()));
    }

    @Test
    @Order(4)
    @DisplayName("Get bookings by name filter")
    public void testGetBookingsByName() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("firstname", "John")
                .queryParam("lastname", "Doe")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(5)
    @DisplayName("Update booking (PUT)")
    public void testUpdateBooking() {
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

        given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + authToken)
                .body(updateBody)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"))
                .body("lastname", equalTo("Smith"))
                .body("totalprice", equalTo(200))
                .body("depositpaid", equalTo(false));
    }

    @Test
    @Order(6)
    @DisplayName("Partial update booking (PATCH)")
    public void testPartialUpdateBooking() {
        String partialUpdate = "{\n" +
                "    \"firstname\": \"Michael\",\n" +
                "    \"lastname\": \"Johnson\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + authToken)
                .body(partialUpdate)
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Michael"))
                .body("lastname", equalTo("Johnson"));
    }

    @Test
    @Order(7)
    @DisplayName("Delete booking")
    public void testDeleteBooking() {
        given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + authToken)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);

        // Verify deletion
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(8)
    @DisplayName("Health check ping")
    public void testHealthCheck() {
        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(9)
    @DisplayName("Create booking with invalid data")
    public void testCreateBookingWithInvalidData() {
        String invalidBody = "{\n" +
                "    \"firstname\": \"Test\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when()
                .post("/booking")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(10)
    @DisplayName("Get non-existent booking")
    public void testGetNonExistentBooking() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/booking/999999")
                .then()
                .statusCode(404);
    }
}

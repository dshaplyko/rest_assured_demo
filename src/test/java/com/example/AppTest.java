package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class AppTest {

    private Properties config;

    @BeforeClass
    public void setUp() throws IOException {
        config = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        config.load(fis);
        RestAssured.baseURI = config.getProperty("baseUrl");
    }

    // Helper method to read JSON file as a String
    private String readFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return IOUtils.toString(classLoader.getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    @Test
    public void testScheduleDelivery() throws IOException {
        String requestBody = readFileAsString("request/ScheduleDeliveryRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/location/123/delivery")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on the expected response structure
        Assert.assertTrue(responseBody.contains("\"deliveryId\":"));
        Assert.assertTrue(responseBody.contains("\"status\":"));
        Assert.assertTrue(responseBody.contains("\"dasherStatus\":"));
        Assert.assertTrue(responseBody.contains("\"statusUrl\":"));
        Assert.assertTrue(responseBody.contains("\"instructionsFromCustomer\":"));

        // Validating presence and correctness of specific fields
        Integer deliveryId = response.path("deliveryId");
        Assert.assertNotNull(deliveryId, "deliveryId is null");
        Assert.assertTrue(deliveryId > 0, "deliveryId should be greater than 0");

        String status = response.path("status");
        Assert.assertNotNull(status, "status is null");

        String dasherStatus = response.path("dasherStatus");
        Assert.assertNotNull(dasherStatus, "dasherStatus is null");

        String statusUrl = response.path("statusUrl");
        Assert.assertNotNull(statusUrl, "statusUrl is null");

        String instructionsFromCustomer = response.path("instructionsFromCustomer");
        Assert.assertNotNull(instructionsFromCustomer, "instructionsFromCustomer is null");

        // Additional Checks per Specification
        // Ensure the times set are in the future (example check)
        String estimatedPickupTime = response.path("estimatedPickupTime");
        Assert.assertTrue(isTimeInFuture(estimatedPickupTime), "estimatedPickupTime should be in the future");

        String estimatedDeliveryTime = response.path("estimatedDeliveryTime");
        Assert.assertTrue(isTimeInFuture(estimatedDeliveryTime), "estimatedDeliveryTime should be in the future");
    }

    @Test
    public void testUpdateDeliveryStatus() throws IOException {
        String requestBody = readFileAsString("request/UpdateDeliveryStatusRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/delivery")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on the expected response structure
        Assert.assertTrue(responseBody.contains("\"message\":"));
        Assert.assertTrue(responseBody.contains("\"messageId\":"));

        // Validating presence and correctness of specific fields
        String message = response.path("message");
        Assert.assertNotNull(message, "message is null");

        String messageId = response.path("messageId");
        Assert.assertNotNull(messageId, "messageId is null");
    }

    @Test
    public void testCancelDelivery() throws IOException {
        String requestBody = readFileAsString("request/CancelDeliveryRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/delivery/123/cancel")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on the expected response structure
        Assert.assertTrue(responseBody.contains("\"returnInitiated\":"));
        Assert.assertTrue(responseBody.contains("\"cancelledAt\":"));

        // Validating presence and correctness of specific fields
        Boolean returnInitiated = response.path("returnInitiated");
        Assert.assertNotNull(returnInitiated, "returnInitiated is null");

        String cancelledAt = response.path("cancelledAt");
        Assert.assertNotNull(cancelledAt, "cancelledAt is null");
        Assert.assertTrue(isTimeInFuture(cancelledAt), "cancelledAt should be in the future");
    }

    @Test
    public void testValidateDeliveryAddress() throws IOException {
        String requestBody = readFileAsString("request/ValidateDeliveryAddressRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/delivery/validate")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Validate that response contains 'pickupLocations' and it is a non-empty array
        List<Object> pickupLocations = response.path("pickupLocations");
        Assert.assertNotNull(pickupLocations, "pickupLocations is null");
        Assert.assertFalse(pickupLocations.isEmpty(), "pickupLocations should not be empty");

        // Example assertions for nested fields within the first location
        String firstPickupId = response.path("pickupLocations[0].id");
        Assert.assertEquals(firstPickupId, "9972", "Mismatch in first pickup location id");

        String firstPickupCity = response.path("pickupLocations[0].contactDetails.address.cityName");
        Assert.assertEquals(firstPickupCity, "OKLAHOMA CITY", "Mismatch in first pickup location city name");

        // Example assertions for the second location if applicable
        String secondPickupId = response.path("pickupLocations[1].id");
        Assert.assertEquals(secondPickupId, "9974", "Mismatch in second pickup location id");

        String secondPickupCity = response.path("pickupLocations[1].contactDetails.address.cityName");
        Assert.assertEquals(secondPickupCity, "Oklahoma City", "Mismatch in second pickup location city name");

        // Additional Checks per Specification
        String deliveryTime = response.path("deliveryDetails.time");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryDetails.time should be in the future");

        String firstPickupTime = response.path("pickupLocations[0].time");
        Assert.assertTrue(isTimeInFuture(firstPickupTime), "pickupDetails.locations[0].time should be in the future");

        Assert.assertTrue(isTimeAfter(deliveryTime, firstPickupTime), "deliveryDetails.time should be after pickupDetails.locations[0].time");
    }

    // Utility methods to validate time constraints; Adjust according to specific implementations
    private boolean isTimeInFuture(String time) {
        // Check if the given time (ISO 8601 format) is in the future
        return java.time.Instant.parse(time).isAfter(java.time.Instant.now());
    }

    private boolean isTimeAfter(String laterTime, String earlierTime) {
        // Check if laterTime (ISO 8601 format) is after earlierTime
        return java.time.Instant.parse(laterTime).isAfter(java.time.Instant.parse(earlierTime));
    }
}
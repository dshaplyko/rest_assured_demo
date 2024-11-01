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
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class EndToEndTest {

    private Properties config;
    private static String deliveryId;

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

    @Test(priority = 1)
    public void testPostDeliveryEstimate() throws IOException {
        String requestBody = readFileAsString("request/PostEstimateRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/delivery/estimate")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on the expected response structure
        Assert.assertTrue(responseBody.contains("\"pickupTime\":"));
        Assert.assertTrue(responseBody.contains("\"deliveryTime\":"));
        Assert.assertTrue(responseBody.contains("\"fee\":"));
        Assert.assertTrue(responseBody.contains("\"currency\":"));
        Assert.assertTrue(responseBody.contains("\"id\":"));

        // Validating presence and correctness of specific fields
        String pickupTime = response.path("pickupTime");
        Assert.assertNotNull(pickupTime, "pickupTime is null");

        String deliveryTime = response.path("deliveryTime");
        Assert.assertNotNull(deliveryTime, "deliveryTime is null");

        Float fee = response.path("fee");
        Assert.assertNotNull(fee, "fee is null");
        Assert.assertTrue(fee > 0, "fee should be greater than 0");

        String currency = response.path("currency");
        Assert.assertEquals(currency, "USD", "Mismatch in currency");

        Integer id = response.path("id");
        Assert.assertNotNull(id, "id is null");
        Assert.assertTrue(id > 0, "id should be greater than 0");

        // Additional Checks per Specification
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");
    }

    @Test(priority = 2)
    public void testPostDeliveryValidate() throws IOException {
        String requestBody = readFileAsString("request/PostValidateRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "MOBILE")
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

    @Test(priority = 3)
    public void testPostScheduleDelivery() throws IOException {
        String requestBody = readFileAsString("request/PostScheduleRequest.json");

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

        // Validating presence and correctness of specific fields
        deliveryId = response.path("deliveryId").toString();
        Assert.assertNotNull(deliveryId, "deliveryId is null");

        String status = response.path("status");
        Assert.assertEquals(status, "scheduled", "Mismatch in status");

        String dasherStatus = response.path("dasherStatus");
        Assert.assertEquals(dasherStatus, "unassigned", "Mismatch in dasherStatus");

        String statusUrl = response.path("statusUrl");
        Assert.assertNotNull(statusUrl, "statusUrl is null");
    }

    @Test(priority = 4)
    public void testPostCancelDelivery() throws IOException {
        String requestBody = readFileAsString("request/PostCancelRequest.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/delivery/" + deliveryId + "/cancel")
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
        Assert.assertTrue(returnInitiated, "returnInitiated should be true");

        String cancelledAt = response.path("cancelledAt");
        Assert.assertNotNull(cancelledAt, "cancelledAt is null");
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
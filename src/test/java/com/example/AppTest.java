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
    public void testPostScheduleDelivery() throws IOException {
        String requestBody = readFileAsString("request/PostScheduleDeliveryRequest.json");

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
        Assert.assertTrue(responseBody.contains("\"deliveryAddress\":"));
        Assert.assertTrue(responseBody.contains("\"estimatedPickupTime\":"));
        Assert.assertTrue(responseBody.contains("\"estimatedDeliveryTime\":"));
        Assert.assertTrue(responseBody.contains("\"customerName\":"));
        Assert.assertTrue(responseBody.contains("\"customerPhoneNumber\":"));
        Assert.assertTrue(responseBody.contains("\"deliveryFee\":"));
        Assert.assertTrue(responseBody.contains("\"driverTip\":"));

        // Validating presence and correctness of specific fields
        Integer deliveryId = response.path("deliveryId");
        Assert.assertNotNull(deliveryId, "deliveryId is null");

        String status = response.path("status");
        Assert.assertNotNull(status, "status is null");

        String dasherStatus = response.path("dasherStatus");
        Assert.assertNotNull(dasherStatus, "dasherStatus is null");

        String statusUrl = response.path("statusUrl");
        Assert.assertNotNull(statusUrl, "statusUrl is null");

        String instructionsFromCustomer = response.path("instructionsFromCustomer");
        Assert.assertNotNull(instructionsFromCustomer, "instructionsFromCustomer is null");

        String estimatedPickupTime = response.path("estimatedPickupTime");
        Assert.assertNotNull(estimatedPickupTime, "estimatedPickupTime is null");

        String estimatedDeliveryTime = response.path("estimatedDeliveryTime");
        Assert.assertNotNull(estimatedDeliveryTime, "estimatedDeliveryTime is null");

        String customerName = response.path("customerName");
        Assert.assertNotNull(customerName, "customerName is null");

        String customerPhoneNumber = response.path("customerPhoneNumber");
        Assert.assertNotNull(customerPhoneNumber, "customerPhoneNumber is null");

        Integer deliveryFee = response.path("deliveryFee");
        Assert.assertNotNull(deliveryFee, "deliveryFee is null");

        Integer driverTip = response.path("driverTip");
        Assert.assertNotNull(driverTip, "driverTip is null");

        // Additional Checks per Specification
        Assert.assertTrue(isTimeInFuture(estimatedPickupTime), "estimatedPickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(estimatedDeliveryTime), "estimatedDeliveryTime should be in the future");
    }

    @Test
    public void testPostScheduleDeliveryInvalid() throws IOException {
        String requestBody = readFileAsString("request/PostScheduleDeliveryRequestInvalid.json");

        Response response = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/brand/SDI/location/123/delivery")
                .then()
                .statusCode(400)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on the expected response structure
        Assert.assertTrue(responseBody.contains("\"errorMessage\":"));
        Assert.assertTrue(responseBody.contains("\"errors\":"));

        // Validating presence and correctness of specific fields
        String errorMessage = response.path("errorMessage");
        Assert.assertNotNull(errorMessage, "errorMessage is null");

        String errors = response.path("errors");
        Assert.assertNotNull(errors, "errors is null");
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
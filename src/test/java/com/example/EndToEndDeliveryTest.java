package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

public class EndToEndDeliveryTest {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://fulfillment-adapter-v1.snc-api.qa.irb.digital";
    }

    // Helper method to read JSON file as a String
    private String readFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return IOUtils.toString(classLoader.getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    @Test
    public void testEndToEndDeliveryScenario() throws IOException {
        // Step 1: Get Delivery Estimates
        String estimateRequestBody = readFileAsString("request/EstimateDeliveryRequest.json");

        Response estimateResponse = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(estimateRequestBody)
                .when()
                .post("/brand/SDI/delivery/estimate")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String estimateResponseBody = estimateResponse.getBody().asString();
        System.out.println("Estimate Response: " + estimateResponseBody);

        // Extracting necessary data from the estimate response
        String estimatedPickupTime = estimateResponse.path("pickupTime");
        String estimatedDeliveryTime = estimateResponse.path("deliveryTime");
        int deliveryFee = estimateResponse.path("fee");

        // Step 2: Validate Delivery
        String validateRequestBody = readFileAsString("request/ValidateDeliveryRequest.json");

        Response validateResponse = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(validateRequestBody)
                .when()
                .post("/brand/SDI/delivery/validate")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String validateResponseBody = validateResponse.getBody().asString();
        System.out.println("Validate Response: " + validateResponseBody);

        // Step 3: Schedule Delivery
        String scheduleRequestBody = readFileAsString("request/ScheduleDeliveryRequest.json");

        Response scheduleResponse = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(scheduleRequestBody)
                .when()
                .post("/brand/SDI/location/123/delivery")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String scheduleResponseBody = scheduleResponse.getBody().asString();
        System.out.println("Schedule Response: " + scheduleResponseBody);

        // Extracting necessary data from the schedule response
        int deliveryId = scheduleResponse.path("deliveryId");

        // Step 4: Cancel Delivery
        String cancelRequestBody = readFileAsString("request/CancelDeliveryRequest.json");

        Response cancelResponse = given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .header("Content-type", "application/json")
                .and()
                .body(cancelRequestBody)
                .when()
                .post("/brand/SDI/delivery/" + deliveryId + "/cancel")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String cancelResponseBody = cancelResponse.getBody().asString();
        System.out.println("Cancel Response: " + cancelResponseBody);

        // Assertions to ensure the end-to-end flow is successful
        Assert.assertNotNull(estimatedPickupTime, "Estimated Pickup Time is null");
        Assert.assertNotNull(estimatedDeliveryTime, "Estimated Delivery Time is null");
        Assert.assertTrue(deliveryFee > 0, "Delivery Fee should be greater than 0");
        Assert.assertNotNull(validateResponseBody, "Validate Response Body is null");
        Assert.assertNotNull(scheduleResponseBody, "Schedule Response Body is null");
        Assert.assertTrue(deliveryId > 0, "Delivery ID should be greater than 0");
        Assert.assertNotNull(cancelResponseBody, "Cancel Response Body is null");
    }
}

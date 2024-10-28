package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

public class AppTest {

    // Helper method to read JSON file as a String
    private String readFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return IOUtils.toString(classLoader.getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    @Test
    public void testPostDeliveryEstimate() throws IOException {
        RestAssured.baseURI = "https://fulfillment-adapter-v1.snc-api.qa.irb.digital";

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
                .statusCode(201)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Assertions based on expected response structure
        Assert.assertTrue(responseBody.contains("\"pickupDetails\":"));
        Assert.assertTrue(responseBody.contains("\"deliveryDetails\":"));
        Assert.assertTrue(responseBody.contains("\"order\":"));

        // Extract and validate specific fields
        String pickupId = response.path("pickupDetails.id");
        Assert.assertEquals(pickupId, "9972", "Mismatch in pickupDetails.id");

        Float subTotal = response.path("order.subTotal");
        Assert.assertEquals(subTotal, Float.valueOf(19.99f), "Mismatch in order.subTotal");
    }

    @Test
    public void testPostDeliveryValidate() throws IOException {
        RestAssured.baseURI = "https://fulfillment-adapter-v1.snc-api.qa.irb.digital";

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
                .statusCode(201)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);

        // Placeholder assertions; adapt to match your actual response structure and content
        Assert.assertTrue(responseBody.contains("\"pickupDetails\":"));
        Assert.assertTrue(responseBody.contains("\"deliveryDetails\":"));

        // Example assertions for nested fields
        String firstPickupId = response.path("pickupDetails.locations[0].id");
        Assert.assertEquals(firstPickupId, "9972", "Mismatch in first location id");

        String secondPickupCity = response.path("pickupDetails.locations[1].contactDetails.address.cityName");
        Assert.assertEquals(secondPickupCity, "Oklahoma City", "Mismatch in second pickup city name");
    }
}
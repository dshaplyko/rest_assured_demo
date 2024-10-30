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
    public void testPostDeliveryEstimate() throws IOException {
        String requestBody = readFileAsString("request/PostDeliveryEstimateRequest.json");

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

        // Assertions based on expected response structure
        Assert.assertTrue(responseBody.contains("\"pickupTime\":"));
        Assert.assertTrue(responseBody.contains("\"deliveryTime\":"));
        Assert.assertTrue(responseBody.contains("\"fee\":"));

        // Extract and validate specific fields
        String pickupTime = response.path("pickupTime");
        Assert.assertNotNull(pickupTime, "pickupTime should not be null");

        String deliveryTime = response.path("deliveryTime");
        Assert.assertNotNull(deliveryTime, "deliveryTime should not be null");

        Float fee = response.path("fee");
        Assert.assertNotNull(fee, "fee should not be null");
    }

    @Test
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
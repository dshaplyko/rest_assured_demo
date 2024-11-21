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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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

    // Helper method to read JSON file as a String
    private String readFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return IOUtils.toString(classLoader.getResourceAsStream(fileName), StandardCharsets.UTF_8);
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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

    @Test
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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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

    // Utility methods to validate time constraints; Adjust according to specific implementations
    private boolean isTimeInFuture(String time) {
        // Check if the given time (ISO 8601 format) is in the future
        return java.time.Instant.parse(time).isAfter(java.time.Instant.now());
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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

    private boolean isTimeAfter(String laterTime, String earlierTime) {
        // Check if laterTime (ISO 8601 format) is after earlierTime
        return java.time.Instant.parse(laterTime).isAfter(java.time.Instant.parse(earlierTime));
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));
        Assert.assertTrue(responseBody.contains(""));

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
        // Ensure either pickupDetails.time or deliveryDetails.time is set (assuming both are not null in the request)
        // Ensure the times set are in the future (example check)
        Assert.assertTrue(isTimeInFuture(pickupTime), "pickupTime should be in the future");
        Assert.assertTrue(isTimeInFuture(deliveryTime), "deliveryTime should be in the future");

        // Both coordinates are mandatory in request (already part of schema, thus assumed valid)
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
package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AppTest {

    @Test
    public void testUpdateDeliveryStatusSuccess() {
        String requestBody = "{\n" +
                "  \"type\": \"status_update\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": \"6059855129\",\n" +
                "      \"status\": \"dispatched\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        given()
                .header("channel-id", "MOBILE_IOS")
                .header("sub-channel-id", "WEB")
                .pathParam("brandId", "BWW")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("https://fulfillment-adapter-v1.snc-api.qa.irb.digital/brand/{brandId}/delivery")
                .then()
                .statusCode(200)
                .body("message", equalTo("ACK"));
    }

    @Test
    public void testUpdateDeliveryStatusInvalidRequest() {
        String requestBody = "{\n" +
                "  \"type\": \"status_update\"\n" +
                "}";

        given()
                .header("channel-id", "MOBILE_IOS")
                .header("sub-channel-id", "WEB")
                .pathParam("brandId", "BWW")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("https://fulfillment-adapter-v1.snc-api.qa.irb.digital/brand/{brandId}/delivery")
                .then()
                .statusCode(400)
                .body("errorMessage", equalTo("Invalid Request"));
    }

    @Test
    public void testUpdateDeliveryStatusInternalError() {
        String requestBody = "{\n" +
                "  \"type\": \"status_update\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": \"6059855129\",\n" +
                "      \"status\": \"dispatched\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        given()
                .header("channel-id", "MOBILE_IOS")
                .header("sub-channel-id", "WEB")
                .pathParam("brandId", "BWW")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("https://fulfillment-adapter-v1.snc-api.qa.irb.digital/brand/{brandId}/delivery")
                .then()
                .statusCode(500)
                .body("errorMessage", equalTo("Internal Server Error"));
    }
}
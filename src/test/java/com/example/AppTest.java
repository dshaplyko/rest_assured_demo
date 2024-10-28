package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AppTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://fulfillment-adapter-v1.snc-api.qa.irb.digital";
    }

    @Test
    public void testUpdateDeliveryStatus() {
        String requestBody = "{\n" +
                "  \"type\": \"update\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": \"6059855129\",\n" +
                "      \"external\": \"15aaedf-b9b3-416e-937e-43329ab221cb\",\n" +
                "      \"status\": \"dispatched\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .pathParam("brandId", "ARB")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/brand/{brandId}/delivery")
                .then()
                .statusCode(200)
                .body("message", equalTo("ACK"));
    }

    @Test
    public void testUpdateDeliveryStatusFromFile() {
        given()
                .header("channel-id", "WEBOA")
                .header("sub-channel-id", "WEB")
                .pathParam("brandId", "ARB")
                .contentType(ContentType.JSON)
                .body(new File("src/test/resources/request/updateDeliveryStatus.json"))
                .when()
                .post("/brand/{brandId}/delivery")
                .then()
                .statusCode(200)
                .body("message", equalTo("ACK"));
    }
}
package com.company.automation.api;

import com.company.automation.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    private ApiClient() {
    }

    public static RequestSpecification request() {
        return RestAssured.given()
                .baseUri(ConfigReader.get("apiBaseUrl"))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    public static Response post(String path, Object body) {
        return request().body(body).post(path);
    }

    public static Response get(String path) {
        return request().get(path);
    }
}

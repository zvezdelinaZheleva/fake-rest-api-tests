package com.fake.api.requests;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseRequest {
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String PUT = "PUT";
    protected static final String DELETE = "DELETE";

    public Response sendRequest(String endpoint, String method, Object id, String payload) {

        RequestSpecification request = given().contentType(ContentType.JSON);

        if (payload != null && !method.equalsIgnoreCase(GET)) {
            request.body(payload);
        }

        switch (method.toUpperCase()) {
            case GET:
                if (id != null) {
                    return given().when().get(String.format("%s/%s", endpoint, id));
                } else {
                    return given().when().get(endpoint);
                }
            case POST:
                return request.when().post(endpoint);
            case PUT:
                return request.when().put(String.format("%s/%s", endpoint, id));
            case DELETE:
                return given()
                        .contentType(ContentType.JSON)
                        .when()
                        .delete(String.format("%s/%s", endpoint, id));
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }
    }
}

package com.fake.api.requests;

import com.fake.api.configs.ApplicationConfigLoader;
import io.restassured.response.Response;

public class AuthorRequests extends BaseRequest {
    private final String AUTHORS_URL = ApplicationConfigLoader.getProperty("authorsUrl");

    public Response retrieveAllAuthors() {
        return sendRequest(AUTHORS_URL, GET, null, null);
    }

    public Response retrieveAuthorById(Object authorId) {
        return sendRequest(AUTHORS_URL, GET, authorId, null);
    }

    public Response addAuthor(String jsonPayload) {
        return sendRequest(AUTHORS_URL, POST, null, jsonPayload);
    }

    public Response updateAuthorById(Object authorId, String jsonPayload) {
        return sendRequest(AUTHORS_URL, PUT, authorId, jsonPayload);
    }

    public Response deleteAuthorById(Object authorId) {
        return sendRequest(AUTHORS_URL, DELETE, authorId, null);
    }

}

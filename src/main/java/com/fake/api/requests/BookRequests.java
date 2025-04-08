package com.fake.api.requests;

import com.fake.api.configs.ApplicationConfigLoader;
import io.restassured.response.Response;

public class BookRequests extends BaseRequest {

    private final String BOOKS_URL = ApplicationConfigLoader.getProperty("booksUrl");

    public Response retrieveAllBooks() {
        return sendRequest(BOOKS_URL, GET, null, null);
    }

    public Response retrieveBookById(Object bookId) {
        return sendRequest(BOOKS_URL, GET, bookId, null);
    }

    public Response addBook(String jsonPayload) {
        return sendRequest(BOOKS_URL, POST, null, jsonPayload);
    }

    public Response updateBookById(Object bookId, String payload) {
        return sendRequest(BOOKS_URL, PUT, bookId, payload);
    }

    public Response deleteBookById(Object bookId) {
        return sendRequest(BOOKS_URL, DELETE, bookId, null);

    }
}

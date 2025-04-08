package error_messages;

import static com.fake.api.constants.ApiErrorConstants.*;

import io.restassured.response.Response;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ErrorMessages {
    public void verifyErrorNotFound(Response response) {
        assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_NOT_FOUND);
        assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
    }

    public void verifyErrorInvalidBookIdDataType(Response response) {
        assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
        assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
        Assert.assertTrue(
                response.jsonPath().getMap(ERROR_MESSAGE_ERRORS_KEY).containsKey(ERROR_MESSAGE_ID_BOOK_KEY),
                "Errors' key does not exist");
    }

    public void verifyErrorInvalidDateDataType(Response response) {
        assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
        assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
        Assert.assertTrue(
                response
                        .jsonPath()
                        .getMap(ERROR_MESSAGE_ERRORS_KEY)
                        .containsKey(ERROR_MESSAGE_PUBLISH_DATE_KEY),
                "Errors' key does not exist");
        String errorMessage =
                response
                        .jsonPath()
                        .getString(
                                String.format(
                                        "%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_PUBLISH_DATE_KEY));
        Assert.assertTrue(
                errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_PUBLISH_DATE),
                "The expected error message is missing");
    }

    public void verifyErrorInvalidPageCountData(Response response) {
        assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
        assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
        Assert.assertTrue(
                response
                        .jsonPath()
                        .getMap(ERROR_MESSAGE_ERRORS_KEY)
                        .containsKey(ERROR_MESSAGE_PAGE_COUNT_KEY),
                "Errors' key does not exist");
        String errorMessage =
                response
                        .jsonPath()
                        .getString(
                                String.format(
                                        "%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_PAGE_COUNT_KEY));
        Assert.assertTrue(
                errorMessage.contains(ERROR_MESSAGE_COULD_NOT_CONVERT_INT32),
                "The expected error message is missing");
    }

    public void verifyErrorInvalidDataTypeId(Response response, String value) {
        assertEquals(response.jsonPath().getString(ERROR_MESSAGE_TITLE_KEY), ERROR_MESSAGE_TITLE);
        assertNotNull(response.jsonPath().get(ERROR_MESSAGE_TRACEID_KEY));
        Assert.assertTrue(
                response.jsonPath().getMap(ERROR_MESSAGE_ERRORS_KEY).containsKey(ERROR_MESSAGE_ID_KEY),
                "Errors' key does not exist");
        String errorMessage =
                response
                        .jsonPath()
                        .getString(String.format("%s.'%s'[0]", ERROR_MESSAGE_ERRORS_KEY, ERROR_MESSAGE_ID_KEY));
        Assert.assertTrue(
                errorMessage.contains(String.format(ERROR_MESSAGE_INVALID_VALUE, value)),
                "The expected error message is missing");
    }
}

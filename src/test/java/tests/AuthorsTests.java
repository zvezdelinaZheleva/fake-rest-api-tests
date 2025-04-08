package tests;

import com.fake.api.configs.ApplicationConfigLoader;
import com.fake.api.models.Author;
import com.fake.api.repository.AuthorsRepository;
import com.fake.api.requests.AuthorRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import error_messages.ErrorMessages;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.fake.api.constants.ApiErrorConstants.INVALID_ID_DATA_TYPE;
import static com.fake.api.constants.ApiErrorConstants.NONEXISTENT_ID;

public class AuthorsTests {
    private AuthorsRepository authorsRepository;
    private Author expectedAuthor;
    private AuthorRequests authorRequests;
    private ErrorMessages verifyError;
    private Gson gson;

    @BeforeClass
    public void beforeClass() {

        RestAssured.baseURI = ApplicationConfigLoader.getProperty("baseUrl");
        authorsRepository = new AuthorsRepository();
        authorRequests = new AuthorRequests();
        authorsRepository.loadAuthors("authors.json");
        expectedAuthor = authorsRepository.getAllAuthors().get(0);
        verifyError = new ErrorMessages();
        gson = new GsonBuilder().create();
    }

    @Test(description = "Retrieve all the authors")
    public void testGetAllAuthors() {
        Response response =
                authorRequests.retrieveAllAuthors().then().statusCode(HttpStatus.SC_OK).extract().response();

        Assert.assertFalse(
                response.jsonPath().getList("id").isEmpty(), "Authors' list should not be empty");
    }

    @Test(description = "Retrieve details of a specific author by its ID")
    public void testRetrieveAuthorById() {
        Author responseAuthor =
                authorRequests
                        .retrieveAuthorById(expectedAuthor.getId())
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response()
                        .as(Author.class);
        Assert.assertEquals(
                responseAuthor.getId(),
                expectedAuthor.getId(),
                String.format(
                        "The expected author' id is %s, but we have %s",
                        expectedAuthor.getId(), responseAuthor.getId()));
        Assert.assertEquals(
                responseAuthor.getIdBook(),
                expectedAuthor.getIdBook(),
                String.format(
                        "The expected author' bookId is %s, but we have %s",
                        expectedAuthor.getIdBook(), responseAuthor.getIdBook()));
        Assert.assertEquals(
                responseAuthor.getFirstName(),
                expectedAuthor.getFirstName(),
                String.format(
                        "The expected author' firstName is %s, but we have %s",
                        expectedAuthor.getFirstName(), responseAuthor.getFirstName()));
        Assert.assertEquals(
                responseAuthor.getLastName(),
                expectedAuthor.getLastName(),
                String.format(
                        "The expected author' lastName is %s, but we got %s",
                        expectedAuthor.getLastName(), responseAuthor.getLastName()));
    }

    @Test(description = "Retrieve an author by nonexistent id")
    public void testRetrieveAuthorByIdNotFound() {
        Response response =
                authorRequests
                        .retrieveAuthorById(NONEXISTENT_ID)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

    @Test(description = "Add an author to the system")
    public void testAddValidAuthor() {

        Author newValidAuthor = authorsRepository.getFakeAuthor();
        String jsonPayLoad = gson.toJson(newValidAuthor);
        Response response = authorRequests.addAuthor(jsonPayLoad).then().extract().response();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertNotNull(response.jsonPath().getString("id"), "Id should not be null");

        Author createdAuthor = response.as(Author.class);
        Author responseAuthor =
                authorRequests
                        .retrieveAuthorById(createdAuthor.getId())
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response()
                        .as(Author.class);
        Assert.assertEquals(responseAuthor.getId(), createdAuthor.getId());
    }

    @Test(description = "Add an author with not valid data type")
    public void testAddAuthorWithInvalidDataType() {

        Author newAuthor = authorsRepository.getFakeAuthor();
        newAuthor.setIdBook(INVALID_ID_DATA_TYPE);
        String jsonPayLoad = gson.toJson(newAuthor);
        Response response = authorRequests.addAuthor(jsonPayLoad).then().extract().response();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "Update an existing author's details")
    public void updateAuthorById() {
        Author author = authorsRepository.getFakeAuthor();
        author.setId(expectedAuthor.getId());
        String payload = gson.toJson(author);

        Author updatedAuthor =
                authorRequests
                        .updateAuthorById(author.getId(), payload)
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response()
                        .as(Author.class);

        Assert.assertEquals(
                updatedAuthor.getId(),
                author.getId(),
                String.format(
                        "The expected author' id is %s, but we have %s",
                        author.getId(), updatedAuthor.getId()));
        Assert.assertEquals(
                updatedAuthor.getIdBook(),
                author.getIdBook(),
                String.format(
                        "The expected author' book id is %s, but we have %s",
                        author.getIdBook(), updatedAuthor.getIdBook()));
        Assert.assertEquals(
                updatedAuthor.getFirstName(),
                author.getFirstName(),
                String.format(
                        "The expected author' firstname is %s, but we have %s",
                        author.getFirstName(), updatedAuthor.getFirstName()));
        Assert.assertEquals(
                updatedAuthor.getLastName(),
                author.getLastName(),
                String.format(
                        "The expected author' lastname is %s, but we have %s",
                        author.getLastName(), updatedAuthor.getLastName()));

    }

    @Test(description = "Update an author with nonexistent id ")
    public void updateAuthorByIdNotFound() {
        Author author = authorsRepository.getFakeAuthor();
        author.setId(expectedAuthor.getId());
        String payload = gson.toJson(author);
        Response response =
                authorRequests
                        .updateAuthorById(NONEXISTENT_ID, payload)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

    @Test(description = "Update an existing author with not valid 'idBook' data type")
    public void updateAuthorInvalidDataType() {
        Author author = authorsRepository.getFakeAuthor();
        author.setId(expectedAuthor.getId());
        author.setIdBook(INVALID_ID_DATA_TYPE);
        String payload = gson.toJson(author);
        Response response =
                authorRequests
                        .updateAuthorById(author.getId(), payload)
                        .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract()
                        .response();
        verifyError.verifyErrorInvalidBookIdDataType(response);
    }

    @Test(description = "Delete an author by valid id")
    public void testDeleteAuthorByValidId() {
        authorRequests.deleteAuthorById(expectedAuthor.getId()).then().statusCode(HttpStatus.SC_OK);
    }

    @Test(description = "Delete an author by nonexistent id")
    public void testDeleteAuthorByNonexistentId() {
        Response response =
                authorRequests
                        .deleteAuthorById(NONEXISTENT_ID)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

    @Test(description = "Delete an author by not valid data type")
    public void testDeleteAuthorByInvalidDataType() {
        Response response = authorRequests.deleteAuthorById(INVALID_ID_DATA_TYPE).then().extract().response();
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
    }

}
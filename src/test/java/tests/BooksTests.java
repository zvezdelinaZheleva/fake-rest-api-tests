package tests;

import com.fake.api.configs.ApplicationConfigLoader;
import com.fake.api.models.Book;
import com.fake.api.repository.BooksRepository;
import com.fake.api.requests.BookRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import error_messages.ErrorMessages;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static com.fake.api.constants.ApiErrorConstants.INVALID_ID_DATA_TYPE;
import static com.fake.api.constants.ApiErrorConstants.NONEXISTENT_ID;
import static org.testng.Assert.*;

public class BooksTests {

    @DataProvider(name = "pageCountData")
    public static Object[][] providePageCountData() {
        return new Object[][]{
                {1, HttpStatus.SC_OK, "Minimum value (per some requirement)"},
                {0, HttpStatus.SC_BAD_REQUEST, "Below the minimum value"},
                {20000, HttpStatus.SC_OK, "Max value (per some requirement)"},
                {20001, HttpStatus.SC_BAD_REQUEST, "Above the max value"},
                {"fifty-five", HttpStatus.SC_BAD_REQUEST, "String instead of integer value"},
                {2147483647, HttpStatus.SC_OK, "Maximum 32-bit integer value"},
                {2147483648L, HttpStatus.SC_BAD_REQUEST, "Exceeding maximum integer value (64-bit)"},
        };
    }

    private BookRequests bookRequests;
    private BooksRepository booksRepository;
    private Book expectedBook;
    private ErrorMessages verifyError;
    private Gson gson;

    @BeforeClass
    public void beforeClass() {

        RestAssured.baseURI = ApplicationConfigLoader.getProperty("baseUrl");
        booksRepository = new BooksRepository();
        booksRepository.loadBooks("books.json");
        expectedBook = booksRepository.getAllBooks().get(0);
        bookRequests = new BookRequests();
        verifyError = new ErrorMessages();
        gson = new GsonBuilder().create();
    }

    @Test(description = "Retrieve all the books")
    public void testRetrieveAllBooks() {
        Response response =
                bookRequests.retrieveAllBooks().then().statusCode(HttpStatus.SC_OK).extract().response();
        assertFalse(response.jsonPath().getList("id").isEmpty(), "Books list should not be empty");

        int totalBooks = response.jsonPath().getList("id").size();
        assertEquals(totalBooks, 200, "Total number of books should be 200");
    }

    @Test(description = "Retrieve details of a specific book by its ID")
    public void testRetrieveBookById() {
        Book responseBook =
                bookRequests
                        .retrieveBookById(expectedBook.getId())
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response()
                        .as(Book.class);
        assertEquals(
                responseBook.getId(),
                expectedBook.getId(),
                String.format(
                        "The expected book id is %s, but we have %s",
                        expectedBook.getId(), responseBook.getId()));
        assertFalse(responseBook.getTitle().isEmpty(), "The book title should not be empty");
        assertEquals(
                responseBook.getTitle(),
                expectedBook.getTitle(),
                String.format(
                        "The expected title is %s, but we have %s",
                        expectedBook.getTitle(), responseBook.getTitle()));
        assertFalse(responseBook.getDescription().isEmpty(), "The book description should not be empty");
        assertEquals(
                responseBook.getPageCount(),
                expectedBook.getPageCount(),
                String.format(
                        "The expected page count is %s, but we have %s",
                        expectedBook.getPageCount(), responseBook.getPageCount()));
        assertFalse(responseBook.getExcerpt().isEmpty(), "The excerpt should not be empty");
        assertFalse(responseBook.getPublishDate().isEmpty(), "The publication date should not be empty");

    }

    @Test(description = "Retrieve a book by nonexistent ID")
    public void testRetrieveBookByIdNotFound() {
        Response response =
                bookRequests
                        .retrieveBookById(NONEXISTENT_ID)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

    @Test(description = "Add new book to the system")
    public void testAddValidBook() {
        Book newValidBook = booksRepository.getFakeBook();
        String newBookJson = gson.toJson(newValidBook);
        Response response = bookRequests.addBook(newBookJson).then().extract().response();
        assertEquals(response.statusCode(), HttpStatus.SC_OK);

        Book createdBook = response.as(Book.class);
        assertNotNull(createdBook.getId(), "The book id should be populated");
        assertEquals(
                createdBook.getTitle(),
                newValidBook.getTitle(),
                String.format(
                        "The expected title of the book is %s, but we have %s",
                        newValidBook.getTitle(), createdBook.getTitle()));
        assertFalse(createdBook.getDescription().isEmpty(), "The book description should be populated");
        assertEquals(
                createdBook.getPageCount(),
                newValidBook.getPageCount(),
                String.format(
                        "The expected page count is %s, but we have %s",
                        newValidBook.getPageCount(), createdBook.getPageCount()));
        assertFalse(createdBook.getExcerpt().isEmpty(), "The excerpt should should be populated");
        assertFalse(createdBook.getPublishDate().isEmpty(), "The publication date should be populated");
    }

    @Test(description = "Add a book with not valid date data type")
    public void testAddBookWithInvalidDataType() {
        Book newBook = booksRepository.getFakeBook();
        newBook.setPublishDate(new Date().toString());
        String newBookJson = gson.toJson(newBook);
        Response response =
                bookRequests
                        .addBook(newBookJson)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();
        verifyError.verifyErrorInvalidDateDataType(response);
    }

    @Test(
            description = "Add a book with different page count values",
            dataProvider = "pageCountData")
    public void testAddBookWithPageCount(
            Object pageCount, int expectedStatus, String description) {
        Book newBook = booksRepository.getFakeBook();
        newBook.setPageCount(pageCount);
        String newBookJson = gson.toJson(newBook);
        Response response = bookRequests.addBook(newBookJson).then().extract().response();
        assertEquals(
                response.statusCode(),
                expectedStatus,
                String.format(
                        "Expected status %d, but we have %d. Description: %s",
                        expectedStatus, response.getStatusCode(), description));
        if (expectedStatus == HttpStatus.SC_BAD_REQUEST) {
            verifyError.verifyErrorInvalidPageCountData(response);
        } else {
            assertEquals(response.jsonPath().getInt("pageCount"), (Integer) pageCount);
        }
    }

    @Test(description = "Update an existing book by its ID")
    public void testUpdateBookById() {
        Book newBook = booksRepository.getFakeBook();
        newBook.setId(expectedBook.getId());
        String newBookJson = gson.toJson(newBook);
        Book updatedBook =
                bookRequests
                        .updateBookById(newBook.getId(), newBookJson)
                        .then()
                        .extract()
                        .response()
                        .as(Book.class);
        assertEquals(
                updatedBook.getId(),
                newBook.getId(),
                String.format(
                        "The expected book id is %s, but we have %s", newBook.getId(), updatedBook.getId()));
        assertFalse(updatedBook.getTitle().isEmpty(), "The book title should not be empty");
        assertEquals(
                updatedBook.getTitle(),
                newBook.getTitle(),
                String.format(
                        "The expected title is %s, but we have %s",
                        newBook.getTitle(), updatedBook.getTitle()));
        assertFalse(updatedBook.getDescription().isEmpty(), "The book description should not be empty");
        assertEquals(
                updatedBook.getPageCount(),
                newBook.getPageCount(),
                String.format(
                        "The expected page count is %s, but we have %s",
                        newBook.getPageCount(), updatedBook.getPageCount()));
        assertFalse(updatedBook.getExcerpt().isEmpty(), "The excerpt should not be empty");
        assertFalse(updatedBook.getPublishDate().isEmpty(), "The publication date should not be empty");
    }

    @Test(description = "Update an existing book with not valid data type for 'id'")
    public void testUpdateBookWithInvalidDataType() {
        Book newBook = booksRepository.getFakeBook();
        String newBookJson = gson.toJson(newBook);
        Response response =
                bookRequests
                        .updateBookById(INVALID_ID_DATA_TYPE, newBookJson)
                        .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract()
                        .response();
        verifyError.verifyErrorInvalidDataTypeId(response, INVALID_ID_DATA_TYPE);
    }

    @Test(description = "Update a book by nonexistent id")
    public void testUpdateBookByNonexistentId() {
        Book newBook = booksRepository.getFakeBook();
        String newBookJson = gson.toJson(newBook);
        Response response =
                bookRequests
                        .updateBookById(NONEXISTENT_ID, newBookJson)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

    @Test(
            description = "Update a book with different page count values",
            dataProvider = "pageCountData")
    public void testUpdateBookWithPageCount(
            Object pageCount, int expectedStatus, String description) {
        Book book = booksRepository.getFakeBook();
        book.setId(expectedBook.getId());
        book.setPageCount(pageCount);
        String newBookJson = gson.toJson(book);
        Response response =
                bookRequests.updateBookById(book.getId(), newBookJson).then().extract().response();
        assertEquals(
                response.statusCode(),
                expectedStatus,
                "Expected status " + expectedStatus + ", but we have " + response.getStatusCode() + ". Description: " + description);
        if (expectedStatus == HttpStatus.SC_BAD_REQUEST) {
            verifyError.verifyErrorInvalidPageCountData(response);
        } else {
            assertEquals(response.jsonPath().getInt("pageCount"), (Integer) pageCount);
        }
    }

    @Test(description = "Delete an existing book by its ID")
    public void testDeleteBookById() {
        bookRequests.deleteBookById(expectedBook.getId()).then().statusCode(HttpStatus.SC_OK);
        bookRequests.retrieveBookById(expectedBook.getId()).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "Delete a book with not valid data type for 'id' ")
    public void testDeleteBookInvalidDataTypeId() {
        Response response =
                bookRequests
                        .deleteBookById(INVALID_ID_DATA_TYPE)
                        .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract()
                        .response();
        verifyError.verifyErrorInvalidDataTypeId(response, INVALID_ID_DATA_TYPE);
    }

    @Test(description = "Delete a book by nonexistent id")
    public void testDeleteBookNonexistentId() {
        Response response =
                bookRequests
                        .deleteBookById(NONEXISTENT_ID)
                        .then()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .extract()
                        .response();
        verifyError.verifyErrorNotFound(response);
    }

}

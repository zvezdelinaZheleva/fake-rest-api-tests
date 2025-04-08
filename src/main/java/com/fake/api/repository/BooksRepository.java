package com.fake.api.repository;

import com.fake.api.models.Book;
import net.datafaker.Faker;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BooksRepository {
    private final Faker faker = new Faker();
    private final JsonLoader loadData = new JsonLoader();
    private static final String BOOKS_DATA_PATH = "data/books/";
    private List<Book> books;

    public Book getFakeBook() {

        Instant pastInstantDate = faker.date().past(365, TimeUnit.DAYS).toInstant();
        String formattedPublishDate =
                DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(pastInstantDate);

        return Book.builder()
                .title(faker.book().title())
                .description(faker.lorem().paragraph())
                .pageCount(faker.number().numberBetween(0, 20000))
                .excerpt(faker.lorem().paragraph())
                .publishDate(formattedPublishDate)
                .build();

    }

    public void loadBooks(String resourceName) {
        books = loadData.loadObjects(String.format("%s/%s", BOOKS_DATA_PATH, resourceName), Book.class);
    }

    public List<Book> getAllBooks() {
        return books;
    }

}

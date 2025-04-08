package com.fake.api.repository;

import com.fake.api.models.Author;
import net.datafaker.Faker;

import java.util.List;


public class AuthorsRepository {
    private final Faker faker = new Faker();
    private final JsonLoader loadData = new JsonLoader();
    private static final String AUTHORS_DATA_PATH = "data/authors/";
    private List<Author> authors;

    public Author getFakeAuthor() {

        return Author.builder()
                .idBook(faker.number().numberBetween(0, 1000))
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    public void loadAuthors(String resourceName) {
        authors = loadData.loadObjects(String.format("%s/%s", AUTHORS_DATA_PATH, resourceName), Author.class);
    }

    public List<Author> getAllAuthors() {
        return authors;
    }


}

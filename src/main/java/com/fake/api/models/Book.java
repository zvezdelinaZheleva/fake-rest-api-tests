package com.fake.api.models;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Object id;
    private String title;
    private String description;
    private Object pageCount;
    private String excerpt;
    private String publishDate;

}

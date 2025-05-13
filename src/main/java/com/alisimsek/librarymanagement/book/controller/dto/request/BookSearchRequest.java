package com.alisimsek.librarymanagement.book.controller.dto.request;

import com.alisimsek.librarymanagement.common.base.EntityStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookSearchRequest {
    private String title;
    private String author;
    private String isbn;
    private LocalDate publicationDate;
    private String genre;
    private boolean available = true;
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
}

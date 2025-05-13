package com.alisimsek.librarymanagement.book;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookCreateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookUpdateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.common.base.EntityStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BookTestProvider {

    public static BookCreateRequest createBookRequest() {
        return BookCreateRequest.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9781234567897")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .genre("Fiction")
                .description("Test description")
                .build();
    }

    public static BookUpdateRequest updateBookRequest() {
        return BookUpdateRequest.builder()
                .title("Updated Book")
                .author("Updated Author")
                .isbn("9789876543210")
                .publicationDate(LocalDate.of(2023, 2, 2))
                .genre("Non-Fiction")
                .description("Updated description")
                .build();
    }

    public static BookSearchRequest searchBookRequest() {
        BookSearchRequest request = new BookSearchRequest();
        request.setTitle("Test");
        request.setAuthor("Author");
        request.setGenre("Fiction");
        request.setAvailable(true);
        return request;
    }

    public static BookDto createBookDto() {
        return BookDto.builder()
                .guid(UUID.randomUUID().toString())
                .title("Test Book")
                .author("Test Author")
                .isbn("9781234567897")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .genre("Fiction")
                .description("Test description")
                .available(true)
                .build();
    }

    public static BookDto createUpdatedBookDto() {
        return BookDto.builder()
                .guid(UUID.randomUUID().toString())
                .title("Updated Book")
                .author("Updated Author")
                .isbn("9789876543210")
                .publicationDate(LocalDate.of(2023, 2, 2))
                .genre("Non-Fiction")
                .description("Updated description")
                .available(true)
                .build();
    }

    public static List<BookDto> createBookDtoList() {
        return Arrays.asList(
                createBookDto(),
                BookDto.builder()
                        .guid(UUID.randomUUID().toString())
                        .title("Another Book")
                        .author("Another Author")
                        .isbn("9780987654321")
                        .publicationDate(LocalDate.of(2022, 5, 5))
                        .genre("Mystery")
                        .description("Another description")
                        .available(true)
                        .build()
        );
    }

    public static Book createBookEntity() {
        Book book = new Book();
        book.setGuid(UUID.randomUUID().toString());
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("9781234567897");
        book.setPublicationDate(LocalDate.of(2023, 1, 1));
        book.setGenre("Fiction");
        book.setDescription("Test description");
        book.setAvailable(true);
        book.setEntityStatus(EntityStatus.ACTIVE);
        return book;
    }

    public static Book createUpdatedBookEntity() {
        Book book = new Book();
        book.setGuid(UUID.randomUUID().toString());
        book.setTitle("Updated Book");
        book.setAuthor("Updated Author");
        book.setIsbn("9789876543210");
        book.setPublicationDate(LocalDate.of(2023, 2, 2));
        book.setGenre("Non-Fiction");
        book.setDescription("Updated description");
        book.setAvailable(true);
        book.setEntityStatus(EntityStatus.ACTIVE);
        return book;
    }
} 
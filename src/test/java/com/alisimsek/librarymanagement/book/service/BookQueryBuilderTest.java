package com.alisimsek.librarymanagement.book.service;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.entity.QBook;
import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookQueryBuilderTest {

    @Test
    void createQuery_WhenAllFieldsProvided_ShouldCreateCompleteQuery() {
        // Given
        LocalDate publicationDate = LocalDate.of(2020, 1, 1);
        BookSearchRequest request = new BookSearchRequest();
        request.setTitle("Test Title");
        request.setAuthor("Test Author");
        request.setIsbn("1234567890");
        request.setPublicationDate(publicationDate);
        request.setGenre("Fiction");
        request.setEntityStatus(EntityStatus.ACTIVE);
        request.setAvailable(true);

        // When
        BooleanBuilder result = BookQueryBuilder.createQuery(request);

        // Then
        assertNotNull(result);
        String queryString = result.toString();
        assertTrue(queryString.contains(QBook.book.title.containsIgnoreCase("Test Title").toString()));
        assertTrue(queryString.contains(QBook.book.author.containsIgnoreCase("Test Author").toString()));
        assertTrue(queryString.contains(QBook.book.isbn.containsIgnoreCase("1234567890").toString()));
        assertTrue(queryString.contains(QBook.book.publicationDate.eq(publicationDate).toString()));
        assertTrue(queryString.contains(QBook.book.genre.containsIgnoreCase("Fiction").toString()));
        assertTrue(queryString.contains(QBook.book.entityStatus.eq(EntityStatus.ACTIVE).toString()));
        assertTrue(queryString.contains(QBook.book.available.eq(true).toString()));
    }

    @Test
    void createQuery_WhenOnlyEntityStatusProvided_ShouldCreateQueryWithEntityStatus() {
        // Given
        BookSearchRequest request = new BookSearchRequest();
        request.setEntityStatus(EntityStatus.DELETED);
        request.setAvailable(false);

        // When
        BooleanBuilder result = BookQueryBuilder.createQuery(request);

        // Then
        assertNotNull(result);
        String queryString = result.toString();
        assertTrue(queryString.contains(QBook.book.entityStatus.eq(EntityStatus.DELETED).toString()));
        assertTrue(queryString.contains(QBook.book.available.eq(false).toString()));
        assertFalse(queryString.contains(QBook.book.title.toString()));
        assertFalse(queryString.contains(QBook.book.author.toString()));
        assertFalse(queryString.contains(QBook.book.isbn.toString()));
        assertFalse(queryString.contains(QBook.book.publicationDate.toString()));
        assertFalse(queryString.contains(QBook.book.genre.toString()));
    }
} 
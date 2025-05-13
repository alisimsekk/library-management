package com.alisimsek.librarymanagement.book.service;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.entity.QBook;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookQueryBuilder {
    public static BooleanBuilder createQuery(BookSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(request.getTitle())){
            builder.and(QBook.book.title.containsIgnoreCase(request.getTitle()));
        }
        if (Objects.nonNull(request.getAuthor())){
            builder.and(QBook.book.author.containsIgnoreCase(request.getAuthor()));
        }
        if (Objects.nonNull(request.getIsbn())){
            builder.and(QBook.book.isbn.containsIgnoreCase(request.getIsbn()));
        }
        if (Objects.nonNull(request.getPublicationDate())) {
            builder.and(QBook.book.publicationDate.eq(request.getPublicationDate()));
        }
        if (Objects.nonNull(request.getGenre())) {
            builder.and(QBook.book.genre.containsIgnoreCase(request.getGenre()));
        }
        if (Objects.nonNull(request.getEntityStatus())) {
            builder.and(QBook.book.entityStatus.eq(request.getEntityStatus()));
        }
        builder.and(QBook.book.available.eq(request.isAvailable()));
        return builder;
    }
}

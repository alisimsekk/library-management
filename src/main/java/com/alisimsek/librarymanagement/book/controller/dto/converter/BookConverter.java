package com.alisimsek.librarymanagement.book.controller.dto.converter;

import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.common.BaseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookConverter extends BaseConverter<Book, BookDto> {

    @Override
    public BookDto convert(Book source) {
        return BookDto.builder()
                .guid(source.getGuid())
                .title(source.getTitle())
                .author(source.getAuthor())
                .isbn(source.getIsbn())
                .publicationDate(source.getPublicationDate())
                .genre(source.getGenre())
                .available(source.isAvailable())
                .description(source.getDescription())
                .build();
    }
}

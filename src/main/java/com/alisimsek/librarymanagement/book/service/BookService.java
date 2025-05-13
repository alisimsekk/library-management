package com.alisimsek.librarymanagement.book.service;

import com.alisimsek.librarymanagement.book.controller.dto.converter.BookConverter;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookCreateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookUpdateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.book.repository.BookRepository;
import com.alisimsek.librarymanagement.common.exception.BusinessException;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    @Transactional
    public BookDto createBook(BookCreateRequest request) {
        bookRepository.findByIsbn(request.getIsbn())
                .ifPresent(existingBook -> {
                    throw new BusinessException("Book with ISBN already exists");
                });

        Book book = Book.create(request.getTitle(), request.getAuthor(), request.getIsbn(), request.getPublicationDate(),
                request.getGenre(), request.getDescription());

        Book savedBook = bookRepository.save(book);
        return bookConverter.convert(savedBook);
    }

    @Transactional(readOnly = true)
    public BookDto getBookByGuid(String guid) {
        Book book = bookRepository.getByGuid(guid);
        return bookConverter.convert(book);
    }

    @Transactional
    public BookDto updateBook(String guid, BookUpdateRequest request) {
        Book book = bookRepository.getByGuid(guid);

        if (request.getIsbn() != null && !request.getIsbn().equals(book.getIsbn())) {
            bookRepository.findByIsbn(request.getIsbn())
                    .ifPresent(existingBook -> {
                        throw new BusinessException("Book with ISBN already exists");
                    });
        }

        book.update(request);
        return bookConverter.convert(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(String guid) {
        Book book = bookRepository.getByGuid(guid);
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> searchBooks(BookSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = BookQueryBuilder.createQuery(request);
        Page<Book> booksPage = bookRepository.findAll(builder, pageable);
        return booksPage.map(bookConverter::convert);
    }

    @Transactional(readOnly = true)
    public boolean isBookAvailable(String guid) {
        Book book = bookRepository.getByGuid(guid);
        return book.isAvailable();
    }

    @Transactional
    public void setBookAvailability(String guid, boolean available) {
        Book book = bookRepository.getByGuid(guid);
        book.setAvailable(available);
        bookRepository.save(book);
    }

} 
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static com.alisimsek.librarymanagement.book.BookTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @InjectMocks
    private BookService bookService;

    private BookCreateRequest createRequest;
    private BookUpdateRequest updateRequest;
    private BookSearchRequest searchRequest;
    private Book book;
    private Book updatedBook;
    private BookDto bookDto;
    private BookDto updatedBookDto;
    private String bookGuid;
    private List<Book> bookList;
    private Page<Book> bookPage;
    private BooleanBuilder booleanBuilder;

    @BeforeEach
    void setUp() {
        createRequest = createBookRequest();
        updateRequest = updateBookRequest();
        searchRequest = searchBookRequest();
        book = createBookEntity();
        updatedBook = createUpdatedBookEntity();
        bookDto = createBookDto();
        updatedBookDto = createUpdatedBookDto();
        bookGuid = book.getGuid();
        bookList = List.of(book, updatedBook);
        bookPage = new PageImpl<>(bookList);
        booleanBuilder = new BooleanBuilder();
    }

    @Test
    void createBook_ShouldReturnBookDto() {
        // Given
        when(bookRepository.findByIsbn(createRequest.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookConverter.convert(book)).thenReturn(bookDto);

        // When
        BookDto result = bookService.createBook(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(bookDto.getTitle(), result.getTitle());
        assertEquals(bookDto.getAuthor(), result.getAuthor());
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        verify(bookRepository).findByIsbn(createRequest.getIsbn());
        verify(bookRepository).save(any(Book.class));
        verify(bookConverter).convert(book);
    }

    @Test
    void createBook_WithExistingIsbn_ShouldThrowException() {
        // Given
        when(bookRepository.findByIsbn(createRequest.getIsbn())).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BusinessException.class, () -> bookService.createBook(createRequest));
        verify(bookRepository).findByIsbn(createRequest.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookByGuid_ShouldReturnBookDto() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);
        when(bookConverter.convert(book)).thenReturn(bookDto);

        // When
        BookDto result = bookService.getBookByGuid(bookGuid);

        // Then
        assertNotNull(result);
        assertEquals(bookDto.getTitle(), result.getTitle());
        assertEquals(bookDto.getAuthor(), result.getAuthor());
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        verify(bookRepository).getByGuid(bookGuid);
        verify(bookConverter).convert(book);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBookDto() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookConverter.convert(updatedBook)).thenReturn(updatedBookDto);

        // When
        BookDto result = bookService.updateBook(bookGuid, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedBookDto.getTitle(), result.getTitle());
        assertEquals(updatedBookDto.getAuthor(), result.getAuthor());
        assertEquals(updatedBookDto.getIsbn(), result.getIsbn());
        verify(bookRepository).getByGuid(bookGuid);
        verify(bookRepository).save(book);
        verify(bookConverter).convert(updatedBook);
    }

    @Test
    void updateBook_WithNewIsbnAlreadyExists_ShouldThrowException() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);
        when(bookRepository.findByIsbn(updateRequest.getIsbn())).thenReturn(Optional.of(updatedBook));

        // When & Then
        assertThrows(BusinessException.class, () -> bookService.updateBook(bookGuid, updateRequest));
        verify(bookRepository).getByGuid(bookGuid);
        verify(bookRepository).findByIsbn(updateRequest.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);

        // When
        bookService.deleteBook(bookGuid);

        // Then
        verify(bookRepository).getByGuid(bookGuid);
        verify(bookRepository).delete(book);
    }

    @Test
    void isBookAvailable_ShouldReturnAvailabilityStatus() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);
        book.setAvailable(true);

        // When
        boolean result = bookService.isBookAvailable(bookGuid);

        // Then
        assertTrue(result);
        verify(bookRepository).getByGuid(bookGuid);
    }

    @Test
    void setBookAvailability_ShouldUpdateAvailability() {
        // Given
        when(bookRepository.getByGuid(bookGuid)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);

        // When
        bookService.setBookAvailability(bookGuid, false);

        // Then
        assertFalse(book.isAvailable());
        verify(bookRepository).getByGuid(bookGuid);
        verify(bookRepository).save(book);
    }
} 
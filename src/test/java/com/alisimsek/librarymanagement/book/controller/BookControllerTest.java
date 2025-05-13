package com.alisimsek.librarymanagement.book.controller;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookCreateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookUpdateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.service.BookService;
import com.alisimsek.librarymanagement.security.WithMockCustomUser;
import com.alisimsek.librarymanagement.user.entity.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.alisimsek.librarymanagement.book.BookTestProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String bookGuid;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate serialization
        bookGuid = UUID.randomUUID().toString();
    }

    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void createBook_ShouldReturnCreatedBook() throws Exception {
        // Given
        BookCreateRequest request = createBookRequest();
        BookDto response = createBookDto();

        when(bookService.createBook(any(BookCreateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.data.isbn").value(response.getIsbn()));

        verify(bookService).createBook(any(BookCreateRequest.class));
    }

    @Test
    void getBook_ShouldReturnBookDetails() throws Exception {
        // Given
        BookDto response = createBookDto();
        when(bookService.getBookByGuid(bookGuid)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/books/{guid}", bookGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.data.isbn").value(response.getIsbn()));

        verify(bookService).getBookByGuid(bookGuid);
    }

    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void updateBook_ShouldReturnUpdatedBook() throws Exception {
        // Given
        BookUpdateRequest request = updateBookRequest();
        BookDto response = createUpdatedBookDto();

        when(bookService.updateBook(eq(bookGuid), any(BookUpdateRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/v1/books/{guid}", bookGuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.data.isbn").value(response.getIsbn()));

        verify(bookService).updateBook(eq(bookGuid), any(BookUpdateRequest.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void deleteBook_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook(bookGuid);

        // When & Then
        mockMvc.perform(delete("/api/v1/books/{guid}", bookGuid))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(bookGuid);
    }

    @Test
    void searchBooks_ShouldReturnBookList() throws Exception {
        // Given
        BookSearchRequest request = searchBookRequest();
        List<BookDto> bookList = createBookDtoList();
        Page<BookDto> bookPage = new PageImpl<>(bookList);

        when(bookService.searchBooks(any(BookSearchRequest.class), any(Pageable.class))).thenReturn(bookPage);

        // When & Then
        mockMvc.perform(get("/api/v1/books/search")
                        .param("title", request.getTitle())
                        .param("author", request.getAuthor())
                        .param("genre", request.getGenre())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(bookList.size()))
                .andExpect(jsonPath("$.data[0].title").value(bookList.get(0).getTitle()));

        verify(bookService).searchBooks(any(BookSearchRequest.class), any(Pageable.class));
    }

    @Test
    void checkAvailability_ShouldReturnAvailabilityStatus() throws Exception {
        // Given
        boolean isAvailable = true;
        when(bookService.isBookAvailable(bookGuid)).thenReturn(isAvailable);

        // When & Then
        mockMvc.perform(get("/api/v1/books/{guid}/availability", bookGuid))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(bookService).isBookAvailable(bookGuid);
    }
} 
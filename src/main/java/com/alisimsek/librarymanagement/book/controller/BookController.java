package com.alisimsek.librarymanagement.book.controller;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookCreateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookSearchRequest;
import com.alisimsek.librarymanagement.book.controller.dto.request.BookUpdateRequest;
import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.service.BookService;
import com.alisimsek.librarymanagement.common.response.ApiResponse;
import com.alisimsek.librarymanagement.common.util.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Controller", description = "Book management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Add a new book", description = "Create a new book with the provided information")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookDto response = bookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{guid}")
    @Operation(summary = "Get book details", description = "Retrieve the details of a specific book by its GUID")
    public ResponseEntity<ApiResponse<BookDto>> getBook(@PathVariable String guid) {
        BookDto response = bookService.getBookByGuid(guid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{guid}")
    @Operation(summary = "Update book information", description = "Update the information of an existing book")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(@PathVariable String guid, @Valid @RequestBody BookUpdateRequest request) {
        BookDto response = bookService.updateBook(guid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{guid}")
    @Operation(summary = "Delete a book", description = "Remove a book from the system")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable String guid) {
        bookService.deleteBook(guid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search for books by title, author, genre, or ISBN")
    public ResponseEntity<ApiResponse<List<BookDto>>> searchBooks(
            @ModelAttribute BookSearchRequest request,Pageable pageable) {
        Page<BookDto> bookDtoPage = bookService.searchBooks(request, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(bookDtoPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(bookDtoPage.getContent()));
    }

    @GetMapping("/{guid}/availability")
    @Operation(summary = "Check book availability", description = "Check if a book is available for borrowing")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String guid) {
        boolean available = bookService.isBookAvailable(guid);
        return ResponseEntity.ok(available);
    }
} 
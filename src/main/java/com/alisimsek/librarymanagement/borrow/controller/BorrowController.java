package com.alisimsek.librarymanagement.borrow.controller;

import com.alisimsek.librarymanagement.borrow.controller.dto.request.BorrowRequest;
import com.alisimsek.librarymanagement.borrow.controller.dto.response.BorrowDto;
import com.alisimsek.librarymanagement.borrow.service.BorrowService;
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
@RequestMapping("/api/v1/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrow Controller", description = "Borrow record management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    @Operation(summary = "Borrow a book", description = "Create a new borrow record for a book")
    @PreAuthorize("hasAuthority('PATRON')")
    public ResponseEntity<ApiResponse<BorrowDto>> borrowBook(@Valid @RequestBody BorrowRequest request) {
        BorrowDto response = borrowService.borrowBook(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{borrowGuid}/return")
    @Operation(summary = "Return a book", description = "Mark a book as returned")
    public ResponseEntity<ApiResponse<BorrowDto>> returnBook(@PathVariable String borrowGuid) {
        BorrowDto response = borrowService.returnBook(borrowGuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{guid}")
    @Operation(summary = "Get borrow details", description = "Get details of a specific borrow record")
    public ResponseEntity<ApiResponse<BorrowDto>> getBorrowById(@PathVariable String guid) {
        BorrowDto response = borrowService.getBorrowById(guid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all borrows", description = "Get a list of all borrow records (librarians and admins only)")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BorrowDto>>> getAllBorrows(Pageable pageable) {
        Page<BorrowDto> borrowDtoPage = borrowService.getBorrowHistory(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(borrowDtoPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(borrowDtoPage.getContent()));
    }

    @GetMapping("/my-history")
    @Operation(summary = "Get my borrow history", description = "Get the current user's borrow history")
    public ResponseEntity<ApiResponse<List<BorrowDto>>> getMyBorrowHistory(Pageable pageable) {
        Page<BorrowDto> borrowDtoPage = borrowService.getCurrentUserBorrowHistory(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(borrowDtoPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(borrowDtoPage.getContent()));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue books", description = "Get a list of all overdue books (librarians and admins only)")
    @PreAuthorize("hasAnyAuthority('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BorrowDto>>> getOverdueBooks(Pageable pageable) {
        Page<BorrowDto> borrowDtoPage = borrowService.getOverdueBooks(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(borrowDtoPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(borrowDtoPage.getContent()));
    }
} 
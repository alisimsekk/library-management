package com.alisimsek.librarymanagement.borrow.controller.dto.response;

import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.borrow.entity.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDto {

    private String guid;
    private BookDto book;
    private String userName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private boolean overdue;
} 
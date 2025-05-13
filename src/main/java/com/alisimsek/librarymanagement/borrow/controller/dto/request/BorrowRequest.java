package com.alisimsek.librarymanagement.borrow.controller.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {

    @NotBlank(message = "Book ID is required")
    private String bookGuid;

    @Min(value = 1, message = "Borrow days must be at least 1")
    @Max(value = 30, message = "Borrow days must be less than 30")
    private int borrowDays;
} 
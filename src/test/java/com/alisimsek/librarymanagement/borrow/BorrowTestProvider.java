package com.alisimsek.librarymanagement.borrow;

import com.alisimsek.librarymanagement.book.controller.dto.response.BookDto;
import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.borrow.controller.dto.request.BorrowRequest;
import com.alisimsek.librarymanagement.borrow.controller.dto.response.BorrowDto;
import com.alisimsek.librarymanagement.borrow.entity.BorrowRecord;
import com.alisimsek.librarymanagement.borrow.entity.BorrowStatus;
import com.alisimsek.librarymanagement.common.base.EntityStatus;
import com.alisimsek.librarymanagement.user.entity.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.alisimsek.librarymanagement.book.BookTestProvider.createBookDto;
import static com.alisimsek.librarymanagement.book.BookTestProvider.createBookEntity;
import static com.alisimsek.librarymanagement.user.UserTestProvider.createUserEntity;

public class BorrowTestProvider {

    public static BorrowRequest createBorrowRequest() {
        return BorrowRequest.builder()
                .bookGuid(UUID.randomUUID().toString())
                .borrowDays(14)
                .build();
    }

    public static BorrowDto createBorrowDto() {
        return BorrowDto.builder()
                .guid(UUID.randomUUID().toString())
                .book(createBookDto())
                .userName("Test User")
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(BorrowStatus.BORROWED)
                .overdue(false)
                .build();
    }

    public static BorrowDto createReturnedBorrowDto() {
        return BorrowDto.builder()
                .guid(UUID.randomUUID().toString())
                .book(createBookDto())
                .userName("Test User")
                .borrowDate(LocalDate.now().minusDays(7))
                .dueDate(LocalDate.now().plusDays(7))
                .returnDate(LocalDate.now())
                .status(BorrowStatus.RETURNED)
                .overdue(false)
                .build();
    }

    public static BorrowDto createOverdueBorrowDto() {
        return BorrowDto.builder()
                .guid(UUID.randomUUID().toString())
                .book(createBookDto())
                .userName("Test User")
                .borrowDate(LocalDate.now().minusDays(30))
                .dueDate(LocalDate.now().minusDays(15))
                .status(BorrowStatus.BORROWED)
                .overdue(true)
                .build();
    }

    public static List<BorrowDto> createBorrowDtoList() {
        return Arrays.asList(
                createBorrowDto(),
                createReturnedBorrowDto(),
                createOverdueBorrowDto()
        );
    }

    public static BorrowRecord createBorrowEntity() {
        Book book = createBookEntity();
        User user = createUserEntity();
        
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setGuid(UUID.randomUUID().toString());
        borrowRecord.setBook(book);
        borrowRecord.setUser(user);
        borrowRecord.setBorrowDate(LocalDate.now());
        borrowRecord.setDueDate(LocalDate.now().plusDays(14));
        borrowRecord.setStatus(BorrowStatus.BORROWED);
        borrowRecord.setEntityStatus(EntityStatus.ACTIVE);
        return borrowRecord;
    }

    public static BorrowRecord createReturnedBorrowEntity() {
        BorrowRecord borrowRecord = createBorrowEntity();
        borrowRecord.setReturnDate(LocalDate.now());
        borrowRecord.setStatus(BorrowStatus.RETURNED);
        return borrowRecord;
    }

    public static BorrowRecord createOverdueBorrowEntity() {
        BorrowRecord borrowRecord = createBorrowEntity();
        borrowRecord.setBorrowDate(LocalDate.now().minusDays(30));
        borrowRecord.setDueDate(LocalDate.now().minusDays(15));
        return borrowRecord;
    }
} 
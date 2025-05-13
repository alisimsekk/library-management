package com.alisimsek.librarymanagement.borrow.entity;

import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.borrow.controller.dto.request.BorrowRequest;
import com.alisimsek.librarymanagement.common.base.BaseEntity;
import com.alisimsek.librarymanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "borrow_record")
public class BorrowRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status;

    public static BorrowRecord create(BorrowRequest request, Book book, User currentUser) {
        BorrowRecord record = new BorrowRecord();
        record.book = book;
        record.user = currentUser;
        record.borrowDate = LocalDate.now();
        int borrowDays = Math.max(request.getBorrowDays(), 1); // Min 1 day
        borrowDays = Math.min(borrowDays, 30); // Max 30 days
        record.setDueDate(LocalDate.now().plusDays(borrowDays));
        record.status = BorrowStatus.BORROWED;
        return record;
    }

    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && LocalDate.now().isAfter(dueDate);
    }

    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = BorrowStatus.RETURNED;
    }
} 
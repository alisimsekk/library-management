package com.alisimsek.librarymanagement.borrow.service;

import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.book.repository.BookRepository;
import com.alisimsek.librarymanagement.borrow.controller.dto.converter.BorrowRecordConverter;
import com.alisimsek.librarymanagement.borrow.controller.dto.request.BorrowRequest;
import com.alisimsek.librarymanagement.borrow.controller.dto.response.BorrowDto;
import com.alisimsek.librarymanagement.borrow.entity.BorrowRecord;
import com.alisimsek.librarymanagement.borrow.entity.BorrowStatus;
import com.alisimsek.librarymanagement.borrow.repository.BorrowRecordRepository;
import com.alisimsek.librarymanagement.common.exception.BusinessException;
import com.alisimsek.librarymanagement.security.CurrentPrincipalProvider;
import com.alisimsek.librarymanagement.user.entity.User;
import com.alisimsek.librarymanagement.user.entity.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository  bookRepository;
    private final BorrowRecordConverter borrowRecordConverter;
    private final CurrentPrincipalProvider currentPrincipalProvider;

    @Transactional
    public BorrowDto borrowBook(BorrowRequest request) {
        User currentUser = currentPrincipalProvider.getCurrentUser();
        if (currentUser.getUserType() != UserType.PATRON) {
            throw new BusinessException("Only patrons can borrow books");
        }
        Book book = bookRepository.getByGuid(request.getBookGuid());
        
        if (!book.isAvailable()) {
            throw new BusinessException("Book is not available for borrowing");
        }
        if (borrowRecordRepository.existsByBookAndStatus(book, BorrowStatus.BORROWED)) {
            throw new BusinessException("Book is already borrowed");
        }

        BorrowRecord borrowRecord = BorrowRecord.create(request, book, currentUser);

        book.setAvailable(false);
        bookRepository.save(book);

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return borrowRecordConverter.convert(savedRecord);
    }

    @Transactional
    public BorrowDto returnBook(String borrowGuid) {
        BorrowRecord borrowRecord = borrowRecordRepository.getByGuid(borrowGuid);
        
        if (borrowRecord.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessException("Book already returned");
        }

        User currentUser = currentPrincipalProvider.getCurrentUser();
        
        if (currentUser.getUserType() == UserType.PATRON && !borrowRecord.getUser().getGuid().equals(currentUser.getGuid())) {
            throw new BusinessException("You can only return books that you borrowed");
        }

        borrowRecord.returnBook();
        
        Book book = borrowRecord.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
        
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return borrowRecordConverter.convert(savedRecord);
    }

    @Transactional(readOnly = true)
    public BorrowDto getBorrowById(String guid) {
        BorrowRecord borrowRecord = borrowRecordRepository.getByGuid(guid);
        return borrowRecordConverter.convert(borrowRecord);
    }

    @Transactional(readOnly = true)
    public Page<BorrowDto> getBorrowHistory(Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.findAll(pageable);
        return borrowRecords.map(borrowRecordConverter::convert);
    }

    @Transactional(readOnly = true)
    public Page<BorrowDto> getCurrentUserBorrowHistory(Pageable pageable) {
        User currentUser = currentPrincipalProvider.getCurrentUser();
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.findByUser(currentUser, pageable);
        return borrowRecords.map(borrowRecordConverter::convert);
    }

    @Transactional(readOnly = true)
    public Page<BorrowDto> getOverdueBooks(Pageable pageable) {
        Page<BorrowRecord> overdueBooks = borrowRecordRepository.findOverdueBooks(LocalDate.now(), pageable);
        return overdueBooks.map(borrowRecordConverter::convert);
    }
} 
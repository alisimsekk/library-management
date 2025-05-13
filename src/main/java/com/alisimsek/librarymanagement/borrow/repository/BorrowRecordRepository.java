package com.alisimsek.librarymanagement.borrow.repository;

import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.borrow.entity.BorrowRecord;
import com.alisimsek.librarymanagement.borrow.entity.BorrowStatus;
import com.alisimsek.librarymanagement.common.base.BaseRepository;
import com.alisimsek.librarymanagement.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends BaseRepository<BorrowRecord, Long> {
    
    Optional<BorrowRecord> findByBookAndStatusNot(Book book, BorrowStatus status);
    
    Page<BorrowRecord> findByUser(User user, Pageable pageable);
    
    Page<BorrowRecord> findByUserAndStatus(User user, BorrowStatus status, Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < ?1")
    Page<BorrowRecord> findOverdueBooks(LocalDate currentDate, Pageable pageable);
    
    List<BorrowRecord> findByBookAndStatus(Book book, BorrowStatus status);
    
    boolean existsByBookAndStatus(Book book, BorrowStatus status);
} 
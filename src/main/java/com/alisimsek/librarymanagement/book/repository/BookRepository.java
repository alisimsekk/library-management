package com.alisimsek.librarymanagement.book.repository;

import com.alisimsek.librarymanagement.book.entity.Book;
import com.alisimsek.librarymanagement.common.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends BaseRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    Page<Book> findByGenreContainingIgnoreCase(String genre, Pageable pageable);
} 
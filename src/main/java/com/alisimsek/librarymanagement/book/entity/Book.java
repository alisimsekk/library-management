package com.alisimsek.librarymanagement.book.entity;

import com.alisimsek.librarymanagement.book.controller.dto.request.BookUpdateRequest;
import com.alisimsek.librarymanagement.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "book")
public class Book extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "genre")
    private String genre;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    @Column(name = "description", length = 2000)
    private String description;


    public static Book create(String title, String author, String isbn,
                              LocalDate publicationDate, String genre, String description) {
        Book book = new Book();
        book.title = title;
        book.author = author;
        book.isbn = isbn;
        book.publicationDate = publicationDate;
        book.genre = genre;
        book.description = description;
        return book;
    }

    public void update(BookUpdateRequest request) {
        this.title = request.getTitle();
        this.author = request.getAuthor();
        this.isbn = request.getIsbn();
        this.publicationDate = request.getPublicationDate();
        this.genre = request.getGenre();
        this.description = request.getDescription();
    }
}
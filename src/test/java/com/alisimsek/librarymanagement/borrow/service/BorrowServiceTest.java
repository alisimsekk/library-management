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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static com.alisimsek.librarymanagement.book.BookTestProvider.createBookEntity;
import static com.alisimsek.librarymanagement.borrow.BorrowTestProvider.*;
import static com.alisimsek.librarymanagement.user.UserTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRecordConverter borrowRecordConverter;

    @Mock
    private CurrentPrincipalProvider currentPrincipalProvider;

    @InjectMocks
    private BorrowService borrowService;

    private BorrowRequest borrowRequest;
    private BorrowRecord borrowRecord;
    private BorrowRecord returnedBorrowRecord;
    private BorrowRecord overdueBorrowRecord;
    private BorrowDto borrowDto;
    private BorrowDto returnedBorrowDto;
    private BorrowDto overdueBorrowDto;
    private Book book;
    private User user;
    private User librarianUser;
    private String borrowGuid;
    private List<BorrowRecord> borrowRecordList;
    private Page<BorrowRecord> borrowRecordPage;

    @BeforeEach
    void setUp() {
        borrowRequest = createBorrowRequest();
        borrowRecord = createBorrowEntity();
        returnedBorrowRecord = createReturnedBorrowEntity();
        overdueBorrowRecord = createOverdueBorrowEntity();
        borrowDto = createBorrowDto();
        returnedBorrowDto = createReturnedBorrowDto();
        overdueBorrowDto = createOverdueBorrowDto();
        book = createBookEntity();
        user = createUserEntity();
        librarianUser = createUpdatedUserEntity(); // This is a LIBRARIAN
        borrowGuid = borrowRecord.getGuid();
        borrowRecordList = List.of(borrowRecord, returnedBorrowRecord, overdueBorrowRecord);
        borrowRecordPage = new PageImpl<>(borrowRecordList);
    }

    @Test
    void borrowBook_AsPatron_ShouldReturnBorrowDto() {
        // Given
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(user);
        when(bookRepository.getByGuid(borrowRequest.getBookGuid())).thenReturn(book);
        when(borrowRecordRepository.existsByBookAndStatus(book, BorrowStatus.BORROWED)).thenReturn(false);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);
        when(borrowRecordConverter.convert(borrowRecord)).thenReturn(borrowDto);

        // When
        BorrowDto result = borrowService.borrowBook(borrowRequest);

        // Then
        assertNotNull(result);
        assertEquals(borrowDto.getGuid(), result.getGuid());
        assertEquals(borrowDto.getUserName(), result.getUserName());
        assertEquals(borrowDto.getStatus(), result.getStatus());
        verify(currentPrincipalProvider).getCurrentUser();
        verify(bookRepository).getByGuid(borrowRequest.getBookGuid());
        verify(borrowRecordRepository).existsByBookAndStatus(book, BorrowStatus.BORROWED);
        verify(bookRepository).save(book);
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
        verify(borrowRecordConverter).convert(borrowRecord);
        assertFalse(book.isAvailable());
    }

    @Test
    void borrowBook_AsLibrarian_ShouldThrowException() {
        // Given
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(librarianUser);

        // When & Then
        assertThrows(BusinessException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(currentPrincipalProvider).getCurrentUser();
        verify(bookRepository, never()).getByGuid(anyString());
    }

    @Test
    void borrowBook_BookNotAvailable_ShouldThrowException() {
        // Given
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(user);
        when(bookRepository.getByGuid(borrowRequest.getBookGuid())).thenReturn(book);
        book.setAvailable(false);

        // When & Then
        assertThrows(BusinessException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(currentPrincipalProvider).getCurrentUser();
        verify(bookRepository).getByGuid(borrowRequest.getBookGuid());
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_BookAlreadyBorrowed_ShouldThrowException() {
        // Given
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(user);
        when(bookRepository.getByGuid(borrowRequest.getBookGuid())).thenReturn(book);
        when(borrowRecordRepository.existsByBookAndStatus(book, BorrowStatus.BORROWED)).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> borrowService.borrowBook(borrowRequest));
        verify(currentPrincipalProvider).getCurrentUser();
        verify(bookRepository).getByGuid(borrowRequest.getBookGuid());
        verify(borrowRecordRepository).existsByBookAndStatus(book, BorrowStatus.BORROWED);
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_ByOwner_ShouldReturnBorrowDto() {
        // Given
        borrowRecord.getUser().setGuid(user.getGuid());
        when(borrowRecordRepository.getByGuid(borrowGuid)).thenReturn(borrowRecord);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(user);
        when(borrowRecordRepository.save(borrowRecord)).thenReturn(returnedBorrowRecord);
        when(borrowRecordConverter.convert(returnedBorrowRecord)).thenReturn(returnedBorrowDto);

        // When
        BorrowDto result = borrowService.returnBook(borrowGuid);

        // Then
        assertNotNull(result);
        assertEquals(returnedBorrowDto.getGuid(), result.getGuid());
        assertEquals(returnedBorrowDto.getStatus(), result.getStatus());
        verify(borrowRecordRepository).getByGuid(borrowGuid);
        verify(currentPrincipalProvider).getCurrentUser();
        verify(borrowRecordRepository).save(borrowRecord);
        verify(borrowRecordConverter).convert(returnedBorrowRecord);
        assertTrue(book.isAvailable());
    }

    @Test
    void returnBook_ByLibrarian_ShouldReturnBorrowDto() {
        // Given
        when(borrowRecordRepository.getByGuid(borrowGuid)).thenReturn(borrowRecord);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(librarianUser);
        when(borrowRecordRepository.save(borrowRecord)).thenReturn(returnedBorrowRecord);
        when(borrowRecordConverter.convert(returnedBorrowRecord)).thenReturn(returnedBorrowDto);

        // When
        BorrowDto result = borrowService.returnBook(borrowGuid);

        // Then
        assertNotNull(result);
        assertEquals(returnedBorrowDto.getGuid(), result.getGuid());
        assertEquals(returnedBorrowDto.getStatus(), result.getStatus());
        verify(borrowRecordRepository).getByGuid(borrowGuid);
        verify(currentPrincipalProvider).getCurrentUser();
        verify(borrowRecordRepository).save(borrowRecord);
        verify(borrowRecordConverter).convert(returnedBorrowRecord);
        assertTrue(book.isAvailable());
    }

    @Test
    void returnBook_AlreadyReturned_ShouldThrowException() {
        // Given
        when(borrowRecordRepository.getByGuid(borrowGuid)).thenReturn(returnedBorrowRecord);

        // When & Then
        assertThrows(BusinessException.class, () -> borrowService.returnBook(borrowGuid));
        verify(borrowRecordRepository).getByGuid(borrowGuid);
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_ByDifferentPatron_ShouldThrowException() {
        // Given
        User differentUser = createUserEntity();
        differentUser.setId(999L); // Different ID
        
        when(borrowRecordRepository.getByGuid(borrowGuid)).thenReturn(borrowRecord);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(differentUser);

        // When & Then
        assertThrows(BusinessException.class, () -> borrowService.returnBook(borrowGuid));
        verify(borrowRecordRepository).getByGuid(borrowGuid);
        verify(currentPrincipalProvider).getCurrentUser();
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }

    @Test
    void getBorrowById_ShouldReturnBorrowDto() {
        // Given
        when(borrowRecordRepository.getByGuid(borrowGuid)).thenReturn(borrowRecord);
        when(borrowRecordConverter.convert(borrowRecord)).thenReturn(borrowDto);

        // When
        BorrowDto result = borrowService.getBorrowById(borrowGuid);

        // Then
        assertNotNull(result);
        assertEquals(borrowDto.getGuid(), result.getGuid());
        assertEquals(borrowDto.getUserName(), result.getUserName());
        assertEquals(borrowDto.getStatus(), result.getStatus());
        verify(borrowRecordRepository).getByGuid(borrowGuid);
        verify(borrowRecordConverter).convert(borrowRecord);
    }

    @Test
    void getBorrowHistory_ShouldReturnBorrowDtoPage() {
        // Given
        when(borrowRecordRepository.findAll(any(Pageable.class))).thenReturn(borrowRecordPage);
        when(borrowRecordConverter.convert(any(BorrowRecord.class)))
                .thenReturn(borrowDto)
                .thenReturn(returnedBorrowDto)
                .thenReturn(overdueBorrowDto);

        // When
        Page<BorrowDto> result = borrowService.getBorrowHistory(mock(Pageable.class));

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(borrowDto.getGuid(), result.getContent().get(0).getGuid());
        assertEquals(returnedBorrowDto.getGuid(), result.getContent().get(1).getGuid());
        assertEquals(overdueBorrowDto.getGuid(), result.getContent().get(2).getGuid());
        verify(borrowRecordRepository).findAll(any(Pageable.class));
        verify(borrowRecordConverter, times(3)).convert(any(BorrowRecord.class));
    }

    @Test
    void getCurrentUserBorrowHistory_ShouldReturnBorrowDtoPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        when(currentPrincipalProvider.getCurrentUser()).thenReturn(user);
        when(borrowRecordRepository.findByUser(user, pageable)).thenReturn(borrowRecordPage);
        when(borrowRecordConverter.convert(any(BorrowRecord.class)))
                .thenReturn(borrowDto)
                .thenReturn(returnedBorrowDto)
                .thenReturn(overdueBorrowDto);

        // When
        Page<BorrowDto> result = borrowService.getCurrentUserBorrowHistory(pageable);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(borrowDto.getGuid(), result.getContent().get(0).getGuid());
        assertEquals(returnedBorrowDto.getGuid(), result.getContent().get(1).getGuid());
        assertEquals(overdueBorrowDto.getGuid(), result.getContent().get(2).getGuid());
        verify(currentPrincipalProvider).getCurrentUser();
        verify(borrowRecordRepository).findByUser(user, pageable);
        verify(borrowRecordConverter, times(3)).convert(any(BorrowRecord.class));
    }

    @Test
    void getOverdueBooks_ShouldReturnBorrowDtoPage() {
        // Given
        when(borrowRecordRepository.findOverdueBooks(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(overdueBorrowRecord)));
        when(borrowRecordConverter.convert(overdueBorrowRecord)).thenReturn(overdueBorrowDto);

        // When
        Page<BorrowDto> result = borrowService.getOverdueBooks(mock(Pageable.class));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(overdueBorrowDto.getGuid(), result.getContent().get(0).getGuid());
        assertTrue(result.getContent().get(0).isOverdue());
        verify(borrowRecordRepository).findOverdueBooks(any(LocalDate.class), any(Pageable.class));
        verify(borrowRecordConverter).convert(overdueBorrowRecord);
    }
} 
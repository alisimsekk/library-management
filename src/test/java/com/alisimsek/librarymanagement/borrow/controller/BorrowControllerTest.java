package com.alisimsek.librarymanagement.borrow.controller;

import com.alisimsek.librarymanagement.borrow.controller.dto.request.BorrowRequest;
import com.alisimsek.librarymanagement.borrow.controller.dto.response.BorrowDto;
import com.alisimsek.librarymanagement.borrow.service.BorrowService;
import com.alisimsek.librarymanagement.security.WithMockCustomUser;
import com.alisimsek.librarymanagement.user.entity.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.alisimsek.librarymanagement.borrow.BorrowTestProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTest {

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BorrowController borrowController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String borrowGuid;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate serialization
        borrowGuid = UUID.randomUUID().toString();
    }

    @Test
    @WithMockCustomUser(userType = UserType.PATRON)
    void borrowBook_ShouldReturnBorrowRecord() throws Exception {
        // Given
        BorrowRequest request = createBorrowRequest();
        BorrowDto response = createBorrowDto();

        when(borrowService.borrowBook(any(BorrowRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.guid").value(response.getGuid()))
                .andExpect(jsonPath("$.data.userName").value(response.getUserName()))
                .andExpect(jsonPath("$.data.status").value(response.getStatus().toString()));

        verify(borrowService).borrowBook(any(BorrowRequest.class));
    }

/*    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void borrowBook_WithLibrarianRole_ShouldFail() throws Exception {
        // Given
        BorrowRequest request = createBorrowRequest();

        // When & Then - This should fail due to incorrect role
        mockMvc.perform(post("/api/v1/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(borrowService, never()).borrowBook(any(BorrowRequest.class));
    }*/

    @Test
    @WithMockCustomUser
    void returnBook_ShouldReturnUpdatedBorrowRecord() throws Exception {
        // Given
        BorrowDto response = createReturnedBorrowDto();

        when(borrowService.returnBook(borrowGuid)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/borrows/{borrowGuid}/return", borrowGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.guid").value(response.getGuid()))
                .andExpect(jsonPath("$.data.status").value(response.getStatus().toString()));

        verify(borrowService).returnBook(borrowGuid);
    }

    @Test
    @WithMockCustomUser
    void getBorrowById_ShouldReturnBorrowDetails() throws Exception {
        // Given
        BorrowDto response = createBorrowDto();

        when(borrowService.getBorrowById(borrowGuid)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/borrows/{guid}", borrowGuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.guid").value(response.getGuid()))
                .andExpect(jsonPath("$.data.userName").value(response.getUserName()))
                .andExpect(jsonPath("$.data.status").value(response.getStatus().toString()));

        verify(borrowService).getBorrowById(borrowGuid);
    }

    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void getAllBorrows_ShouldReturnBorrowList() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<BorrowDto> borrowList = createBorrowDtoList();
        Page<BorrowDto> borrowPage = new PageImpl<>(borrowList, pageable, borrowList.size());

        when(borrowService.getBorrowHistory(any(Pageable.class))).thenReturn(borrowPage);

        // When & Then
        mockMvc.perform(get("/api/v1/borrows")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "borrowDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(borrowList.size()))
                .andExpect(jsonPath("$.data[0].guid").value(borrowList.get(0).getGuid()));

        verify(borrowService).getBorrowHistory(any(Pageable.class));
    }

    @Test
    @WithMockCustomUser
    void getMyBorrowHistory_ShouldReturnUserBorrowList() throws Exception {
        // Given
        List<BorrowDto> borrowList = createBorrowDtoList();
        Page<BorrowDto> borrowPage = new PageImpl<>(borrowList);

        when(borrowService.getCurrentUserBorrowHistory(any(Pageable.class))).thenReturn(borrowPage);

        // When & Then
        mockMvc.perform(get("/api/v1/borrows/my-history")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "borrowDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(borrowList.size()))
                .andExpect(jsonPath("$.data[0].guid").value(borrowList.get(0).getGuid()));

        verify(borrowService).getCurrentUserBorrowHistory(any(Pageable.class));
    }

    @Test
    @WithMockCustomUser(userType = UserType.LIBRARIAN)
    void getOverdueBooks_ShouldReturnOverdueBorrowList() throws Exception {
        // Given
        List<BorrowDto> borrowList = List.of(createOverdueBorrowDto());
        Page<BorrowDto> borrowPage = new PageImpl<>(borrowList);

        when(borrowService.getOverdueBooks(any(Pageable.class))).thenReturn(borrowPage);

        // When & Then
        mockMvc.perform(get("/api/v1/borrows/overdue")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dueDate,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(borrowList.size()))
                .andExpect(jsonPath("$.data[0].overdue").value(true));

        verify(borrowService).getOverdueBooks(any(Pageable.class));
    }
} 
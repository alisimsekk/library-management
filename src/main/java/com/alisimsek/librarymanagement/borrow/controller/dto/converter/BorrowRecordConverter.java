package com.alisimsek.librarymanagement.borrow.controller.dto.converter;

import com.alisimsek.librarymanagement.book.controller.dto.converter.BookConverter;
import com.alisimsek.librarymanagement.borrow.controller.dto.response.BorrowDto;
import com.alisimsek.librarymanagement.borrow.entity.BorrowRecord;
import com.alisimsek.librarymanagement.common.BaseConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BorrowRecordConverter extends BaseConverter<BorrowRecord, BorrowDto> {

    private final BookConverter bookConverter;

    @Override
    public BorrowDto convert(BorrowRecord source) {
        return BorrowDto.builder()
                .guid(source.getGuid())
                .book(bookConverter.convert(source.getBook()))
                .userName(source.getUser().getFirstName() + " " + source.getUser().getLastName())
                .borrowDate(source.getBorrowDate())
                .dueDate(source.getDueDate())
                .returnDate(source.getReturnDate())
                .status(source.getStatus())
                .overdue(source.isOverdue())
                .build();
    }
}

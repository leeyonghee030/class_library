package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedBook {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private LocalDate borrowDate;
}

package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 도서 대출 기록을담는 dto
// dto는  테이블과 1대1 로 맞출 필요는 없다
// SQL 실행 결과를 담는 그릇  임으로 join으로 가져온 컬럼 결과도 담을수있다
public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    private  String title;
    private  String name;
}

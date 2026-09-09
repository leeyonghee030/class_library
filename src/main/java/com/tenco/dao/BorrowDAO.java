package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.BorrowedBook;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 도선 대풀/ 반납 관련 sql 실행
public class BorrowDAO {
    // 현 대출중인 도서 목록 조회 join해서 도서 이름까지 출력

    public List<BorrowedBook> getBorrowedBooks() {
        List<BorrowedBook> borrowedBookList = new ArrayList<>();
        String sql = """
                    select bk.id, bk.title, bk.author, bk.publisher, bk.publication_year, b.borrow_date
                    from borrows b
                    left join books bk
                    on b.book_id = bk.id
                    where return_date is  null;
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs =  pstmt.executeQuery();
             ) {
            while (rs.next()) {
                BorrowedBook borrowedBook = BorrowedBook.builder()
                        .id(rs.getInt("id"))
                        .title(rs.getString("title"))
                        .author(rs.getString("author"))
                        .publisher(rs.getString("publisher"))
                        .publicationYear(rs.getInt("publication_year"))
                        .borrowDate(rs.getDate("borrow_date").toLocalDate())
                        .build();
                borrowedBookList.add(borrowedBook);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return borrowedBookList;
    }

    //도서 대출 기능
    // 대상 도서 대출 기능 여부 - select
    // 도서 대출 기록 - insert

    // 도서 반남처리
    // 대출 기록 확인 select
    // 반납 기록 등록 insert
}

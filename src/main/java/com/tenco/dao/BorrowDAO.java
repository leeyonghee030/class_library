package com.tenco.dao;


import com.tenco.dto.Borrow;
import com.tenco.dto.BorrowedBook;
import com.tenco.util.DatabaseUtil;
import lombok.Data;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 도선 대출/ 반납 관련 sql 실행
public class BorrowDAO {
    // 현 대출중인 도서 목록 조회 join해서 도서 이름까지 출력

    public List<Borrow> getBorrowedBooks() throws SQLException{
        List<Borrow> borrowList = new ArrayList<>();
        String sql = """
                select b.id, b.book_id, bk.title, b.student_id, s.name, b.borrow_date, b.return_date
                from borrows b
                inner join books bk on b.book_id = bk.id
                inner join students s on b.student_id = s.id
                where return_date is null
                order by b.borrow_date;
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();) {
            while (rs.next()) {
                borrowList.add(Borrow.builder()
                                .id(rs.getInt("id"))
                                .bookId(rs.getInt("book_id"))
                                .title(rs.getString("title"))
                                .studentId(rs.getInt("student_id"))
                                .name(rs.getString("name"))
                                .borrowDate(rs.getDate("borrow_date").toLocalDate())
                                .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  borrowList;
    }


//    public List<BorrowedBook> getBorrowedBooks() {
//        List<BorrowedBook> borrowedBookList = new ArrayList<>();
//        String sql = """
//                    select bk.id, bk.title, bk.author, bk.publisher, bk.publication_year, b.borrow_date
//                    from borrows b
//                    left join books bk
//                    on b.book_id = bk.id
//                    where return_date is  null;
//                """;
//        try (Connection conn = DatabaseUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs =  pstmt.executeQuery();
//             ) {
//            while (rs.next()) {
//                BorrowedBook borrowedBook = BorrowedBook.builder()
//                        .id(rs.getInt("id"))
//                        .title(rs.getString("title"))
//                        .author(rs.getString("author"))
//                        .publisher(rs.getString("publisher"))
//                        .publicationYear(rs.getInt("publication_year"))
//                        .borrowDate(rs.getDate("borrow_date").toLocalDate())
//                        .build();
//                borrowedBookList.add(borrowedBook);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return borrowedBookList;
//    }

    //도서 대출 기능
    // 대상 도서 대출 기능 여부 - select
    // 도서 대출 기록 - update
//     처리순서
//     0. 트랙잰션 시작  자동 커밋을끈다
//     1. 대출 가능한지 확인 select
//    2. 책 대출 기록 insert
//    3. 책 대출 하기 update (책 대출 0으로변경 )
//    4. 1-4 이 모두 성공하면 commit, 하나라도 실패하면 rollback
//    5. 자동 커밋을 원래대로 되돌리고 연결을 닫느다
    public  int borrowBook(int bookId, int studentId) throws SQLException {
        int rows = 0;
        Connection conn = null;
        // try-with-resources 로 선언하지않는이유
        // catch 블록에서 rollback 을 호춯 하려면 conn 변수가 catch 안에서도 보여야합니다
        // 그래서 try 바깥에 선언하고 finally에서 직접 닫습니다
        try{
//           1. 트랜잰션 시작
            conn = DatabaseUtil.getConnection();
            // 기본값 autoCommit은 ㅌtrue이고, 이상태에서는 sql 한줄 한줄마다 즉시 확정 반영이됩니다.
            // 이 값을 false로 바꾸면 우리가 commit 을 호출하기 전까지 임시 상태로 남는다
            conn.setAutoCommit(false);

            //2. 대출 가능 여부확인
            String checkSql = """
                    select available from books where id = ?
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("존재하지 않는 도서 입니다 ID : " +bookId );
                    }
                    if (!rs.getBoolean("available")){
                        throw new SQLException("현재 대출 중인 도서입니다 . 반납 후 이용가능합니다");
                    }
                }
            }
            // 3. 코드가 여기까지 내려온다면 대출 가능이다 -> 대출 기록 남기기
            String borrowSql = """
                    insert into borrows (book_id,student_id,borrow_date)
                    values (?, ?, ?)
                    """;
            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
            borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                int row = borrowPstmt.executeUpdate();
            }
            if (rows < 0 ) {
                throw  new SQLException("적용된 기록이 없습니다");
            }

//            4, 도서 상태 변경 (대출 불가로 해당 도서 처리)
            String updateSql = """
                    update books set available =FALSE
                    where id = ?
                    """;
            try (PreparedStatement updatepstmt = conn.prepareStatement(updateSql)) {
                updatepstmt.setInt(1,bookId);
                updatepstmt.executeUpdate();
            }
            // 여기 까지 몯 성공했다면 확정
            conn.commit();
        } catch (Exception e) {
            //5. 하나라도 실패시 롤백 처리
            if (conn != null) {
                conn.rollback();
            }
            throw new RuntimeException(e);
            //conn.rollback();;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // 다시변경 반드시 처리
                conn.close();
            }
        }



        return  rows;
    }




//    public int checkoutBook (int studentId, int bookId) {
//        Connection conn = null;
//        int rows = 0;
//        String selectBookSql = """
//                select title,available
//                from books
//                where id = ? ;
//                """;
//        String updateBookSql = """
//                   update books
//                   set available = "0"
//                   where id = ?;
//                """;
//        String insertBorrowsSql = """
//                   INSERT INTO borrows (book_id, student_id, borrow_date) VALUES
//                   (?, ?,curdate());
//                """;
//        try {
//            conn = DatabaseUtil.getConnection();
//            conn.setAutoCommit(false);
//
//            PreparedStatement selectStmt = conn.prepareStatement(selectBookSql);
//            selectStmt.setInt(1, bookId);
//
//            try (ResultSet rs = selectStmt.executeQuery()) {
//                if (!rs.next()) {
//                    conn.rollback();
//                    System.out.println("책 id를 확인해주세요");
//                    return 0;
//                }
//                if (!rs.getBoolean("available")) {
//                    conn.rollback();
//                    System.out.println("이미 대출중입니다");
//                    return 0;
//                }
//            }
//            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
//                updateStmt.setInt(1, bookId);
//                rows = updateStmt.executeUpdate();
//            }
//            try (PreparedStatement insertStmt = conn.prepareStatement(insertBorrowsSql)) {
//                insertStmt.setInt(1, bookId);
//                insertStmt.setInt(2, studentId);
//                insertStmt.executeUpdate();
//            }
//            conn.commit();
//            return rows;
//        } catch (SQLException e) {
//            if (conn != null) {
//                try {
//                    conn.rollback();
//                } catch (SQLException ex) { /* 무시 */ }
//            }
//            throw new RuntimeException(e);
//        } finally {
//            if (conn != null) {
//                try {
//                    conn.setAutoCommit(true);
//                    conn.close();
//                } catch (SQLException ex) { /* 무시 */ }
//            }
//        }
//    }


    // 도서 반남처리
//    처리 순서
//    1. DB 연결을 업고 자동 커밋을 끈다
//    2. 이 학생이 이 도서를 빌린뒤 아직 반납 하지 않은 기록 있는지 확인
//    3. 찾은 대출 기록의 return_date를 오늘 날짜로 update한다
//    4. books 테이블에 available 을 true로 변경
//    5. 2-4번까지 모두 성공하면 coommit 하나라도 실패시 rollback 처리
//    6. 자동 커밋을 원래대로 되돌리고 연결을 닫는다
    public  int returnBook (int bookId, int studentId)  {
        Connection conn =null;
        int bookRow;

        try { conn = DatabaseUtil.getConnection();
              conn.setAutoCommit(false);
              String checkSql = """
                      select borrow_date, return_date from borrows
                      where book_id = ? and student_id= ? and borrow_date is not null and return_date is null;
                      """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1,bookId);
                checkPstmt.setInt(2,studentId);
                try (ResultSet checkRs = checkPstmt.executeQuery()) {
                    if (!checkRs.next()) {
                        throw new SQLException("대출한 도서가 확인되지않습니다 BookId : " + bookId);
                    }
                }
            }
            String borrowSql = """
                    update borrows
                    set return_date = current_date()
                    where book_id = ? and student_id= ? and borrow_date is not null and return_date is null;
                    """;
            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1,bookId);
                borrowPstmt.setInt(2,studentId);
               int rows = borrowPstmt.executeUpdate();
               if (rows < 1) {
                   throw new SQLException("반납 처리 된걸로 확인됩니다");
               }
            }
            String bookSql = """
                    update books
                    set available = true
                    where id = ?;
                    """;
            try (PreparedStatement bookPstmt = conn.prepareStatement(bookSql)) {
                bookPstmt.setInt(1,bookId);
                bookRow = bookPstmt.executeUpdate();
                if (bookRow < 1) {
                    throw new SQLException("책 반납 가능 수정중 오류 발생했습니다");
                }
            }
            conn.commit();
        } catch (Exception e) {
            if( conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return bookRow;
    }
}

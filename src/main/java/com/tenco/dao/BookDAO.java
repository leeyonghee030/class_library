package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    // 제목으로 도서 검색 기능
    public List<Book> getBookByTitle (String title) {
        List<Book> books = new ArrayList<>();
        String sql = """
            select * from books 
            where title like ?;
""";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) { pstmt.setString(1,"%"+title+"%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next())
                 books.add(createBook(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    //책 전체 조회
    public List<Book> getAllBook() {
        List<Book> bookList = new ArrayList<>();
        String sql = """
                select * from books
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs  = pstmt.executeQuery();) {

           while (rs.next()) {
               bookList.add(createBook(rs));
           }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookList;
    }

    //  책등록
        public int addBook (Book book) {
            int rows = 0;
            String sql = """
                    insert INTO books (title, author, publisher, publication_year, isbn, available) VALUES
                    (?, ?, ?, ?, ?, ?)
                    """;
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);) {
                pstmt.setString(1,book.getTitle());
                pstmt.setString(2, book.getAuthor());
                pstmt.setString(3,book.getPublisher());
                pstmt.setInt(4,book.getPublicationYear());
                pstmt.setString(5, book.getIsbn());
                pstmt.setBoolean(6,book.isAvailable());

                rows = pstmt.executeUpdate();

                System.out.println( rows+"행이 추가되었습니다");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return rows;
        }


    // 책삭제

    //책 수정

    private static Book createBook(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getInt("publication_year"),
                rs.getString("isbn"),
                rs.getBoolean("available")
        );
    }
}

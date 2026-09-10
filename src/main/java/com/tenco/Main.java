package com.tenco;

import com.tenco.dao.BookDAO;
import com.tenco.dao.BorrowDAO;
import com.tenco.dao.StudentDAO;
import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.BorrowedBook;
import com.tenco.dto.Student;

import java.sql.SQLException;
import java.util.List;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) throws SQLException {
        BorrowDAO borrowDAO = new BorrowDAO();
//         List<Borrow> borrowList = borrowDAO.getBorrowedBooks();
//        System.out.println(borrowList);
//         borrowDAO.borrowBook(1,1);
        borrowDAO.returnBook(5,100);

    }


}
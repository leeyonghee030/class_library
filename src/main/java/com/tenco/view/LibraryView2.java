package com.tenco.view;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import com.tenco.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자의 입출력을 처리하는 view 클래스

//역할 키보드 입력을 받아 service에 넘기고 결과를 화면에 출력한다
// sql을 직접 실행하지않고 업무 규칙도 판단 하지않습니다
// 빈값인가 숫자인가 같은 입력 형식을 검사하고 서비스 단에 맞는 객체나 값을 구해서 일을 위임한다 .

public class LibraryView2 {

    private  final LibraryService  libraryService= new LibraryService();
    private  final Scanner scanner = new Scanner(System.in);

    //현재 로그인한 학생 정보가 null 아니라면 로그인 된 상태로 보면된다
    // 만약 null이라면 로그인이 핗요한 기능에서 로그인 요청을 먼저 유도해야한다
    private  Integer currentStudentId =null;
    private  String currentStudenName = null;
    private Student currentStudent = null;

    //프로그램 메인 루프
    // 처리순서
    // 메뉴를 출력한다
    // 번호를 입력 받는다
    // 번호에 맞는 매세드를 호출한다
    // 호출중 Sql Exception이 나면 메세지를 출력하고 다시 1번으로 돌아간다
    // 0번을 입력하면 프로그램 종료
    public void start() {

        System.out.println("==도서 관리 시스템==");
        System.out.println("관리 시스템은 로그인 후 사용 가능합니다");
        while (currentStudenName == null) {
            System.out.println("1.학생등록  2.로그인 0. 종료");
            System.out.print("입력 : ");
            String login = scanner.nextLine();
            if (login.equals("1")) {
                addStudent();
            } else if (login.equals("2")) {
                login();
            } else if (login.equals("0")) {
                return;
            } else {
                System.out.println("0~2 까지의 숫자만 적어주세요");
                continue;
            }        // 학생등록 은 로그인후
        }
        while (currentStudenName != null) {
            System.out.println("==도서 관리 시스템 id : " + currentStudentId +"==");
            //메뉴출력
            System.out.println("""
                    메뉴
                    1.도서추가 기능  2.전체 도서조회 3.제목으로 도서 조회
                    4.전체학생 조회  5.도서대출     6.대출중인 도서조회
                    7.도서 반납     0.종료
                    """);
            System.out.print("입력 : ");
            String num = scanner.nextLine();
            if (num.equals("0")){
                return;
            } else if (num.equals("1")){
                addBook();
            } else if (num.equals("2")){
                getAllBooks();
            } else if (num.equals("3")){
                searchBooksByTitle();
            }else if (num.equals("4")){
                getAllStudent();
            }else if (num.equals("5")){
                borrowBook();
            }else if (num.equals("6")){
                getBorrowedBook();
            }else if (num.equals("7")){
                returnBook();
            } else {
                System.out.println("0 ~ 7 중에 선택 하세요 ");
            }



            //사용자 입력값 받기




        }

    }
    public Student addStudent() {
        while (true) {
        System.out.println("등록할 학생 이름을 적어주세요");
        System.out.println("종료를 원하실경우 0을 입력해주세요");
        System.out.print("입력 : ");
        String name = scanner.nextLine();
        if (name.equals("0")) {
            return null;
        }
        System.out.println("등록할 학번을 적어주세요");
        System.out.print("입력 : ");
        String id = scanner.nextLine();
        if (name == null || name.trim().isEmpty() ||
            id == null || id.trim().isEmpty()) {
            System.out.println("이름과 학번은 필수입니다 다시 적어주세요");
            continue;
        }
        Student student = new Student();
        student.setName(name);
        student.setStudentId(id);
            try {libraryService.addStudent(student);
                System.out.println("등록 완료 되었습니다");
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 학생 등록을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
                continue;
            }
        }
        }
        public void login() {
            while (currentStudenName == null) {
                System.out.println("학번을 입력해주세요");
                System.out.println("종료를 원하실경우 0을 입력해주세요");
                System.out.print("입력 : ");
                currentStudenName = scanner.nextLine();
                if (currentStudenName.equals("0")){
                    currentStudenName = null;
                    return;
                }
                if (currentStudenName == null || currentStudenName.trim().isEmpty()) {
                    System.out.println("학번 입력은 필수입니다");
                    currentStudenName = null;
                    continue;
                }
                try {
                    Student student = libraryService.getStudentByStudentId(currentStudenName);
                    if (student == null) {
                        System.out.println("등록되지 않은 학번입니다. 다시 입력해주세요");
                        currentStudenName = null;
                        continue;}
                    currentStudent = student;
                    currentStudentId = student.getId();
                    System.out.println(student.getName() + "님 환영합니다");
                } catch (SQLException e) {
                    System.out.println("일시적인 오류로 로그인을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                    System.out.println("[디버그] " + e.getMessage());
                    currentStudenName = null;
                    continue;
                }
            }
        }

        public void addBook() {
        while (true) {
            System.out.println("종료를 원하실경우 0을 입력해주세요");
            System.out.println("추가할 책 제목을 입력해주세요");
            System.out.print("입력 : ");
            String title = scanner.nextLine();
            if (title.equals("0")) {
                return;
            }
            System.out.println("추가할 책의 저자를 입력해주세요");
            System.out.print("입력 : ");
            String author = scanner.nextLine();
            if (author.equals("0")) {
                return;
            }
            if (title == null || title.trim().isEmpty() ||
                    author == null || author.trim().isEmpty()) {
                System.out.println("제목과 저자는 필수 입력입니다");
                continue;
            }
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            try {
                libraryService.addBook(book);
                System.out.println(title + "을 등록하였습니다");
                return;
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 책 등록을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
                continue;
            }
        }
        }

        public void getAllBooks() {
            try {
                List<Book> bookList= libraryService.getAllBooks();
                if (bookList.isEmpty()) {
                    System.out.println("등록된 도서가 없습니다");
                    return;
                }
                 for(Book book : bookList){
                    System.out.println(book);
                }
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 전체 책 조회을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
            }
        }

        public void  searchBooksByTitle() {
            while (true) {
                System.out.println("종료를 원하실경우 0을 입력해주세요");
                System.out.println("도서조회할 책 제목을 입력해주세요");
                System.out.print("입력 : ");
                String title = scanner.nextLine();

                if (title.equals("0")){
                    return;
                }
                if (title == null || title.trim().isEmpty()) {
                    System.out.println("제목을 적어주세요");
                    continue;
                }
                try {
                    List<Book> bookList= libraryService.searchBooksByTitle(title);
                    if (bookList.isEmpty()) {
                        System.out.println("등록된 도서가 없습니다");
                        continue;
                    }
                    for(Book book : bookList){
                        System.out.println(book);
                    }
                    return;
                } catch (SQLException e) {
                    System.out.println("일시적인 오류로 책 조회을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                    System.out.println("[디버그] " + e.getMessage());
                    continue;
                }
            }
        }

        public void getAllStudent() {
            try {
               List<Student> studentList = libraryService.getAllStudent();
                if (studentList.isEmpty()) {
                    System.out.println("등록된 학생이 없습니다");
                    return;
                }
               for (Student student : studentList) {
                   System.out.println(student);
               }
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 전체 학생 조회을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
            }
        }


        public void borrowBook() {
        while (true) {
            System.out.println("종료를 원하실경우 0을 입력해주세요");
            System.out.println("도서 대출할 책 ID을 입력해주세요");
            System.out.print("입력 : ");
            String id = scanner.nextLine();
            if (id.equals("0")) {
                return;
            }
            if (id == null || id.trim().isEmpty()) {
                System.out.println("아이디을 적어주세요");
                continue;
            }
            int bookId;
            try {
                bookId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요");
                continue;
            }
            try {
                int row = libraryService.borrowBook(bookId,currentStudentId);
                if (row <= 0) {
                    System.out.println("대출 실패");
                    continue;
                } else {
                    System.out.println("도서 id :" +id +" 을 대출 하였습니다 ");
                }
                return;
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 책 대출을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
                continue;
            }
        }
        }

        public void getBorrowedBook() {
            try {
                List<Borrow> borrowList = libraryService.getBorrowedBook();
                if (borrowList.isEmpty()) {
                    System.out.println("대출중인 도서가 없습니다");
                    return;
                }
                for (Borrow borrow : borrowList) {
                    System.out.println(borrow);
                }
            } catch (SQLException e) {
                System.out.println("일시적인 오류로 대출 목록 조회을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                System.out.println("[디버그] " + e.getMessage());
            }
        }

        public void returnBook() {
            while (true) {
                System.out.println("종료를 원하실경우 0을 입력해주세요");
                System.out.println("반날할 책 ID을 입력해주세요");
                System.out.print("입력 : ");
                String id = scanner.nextLine();
                if (id.equals("0")) {
                    return;
                }
                if (id == null || id.trim().isEmpty()) {
                    System.out.println("아이디을 적어주세요");
                    continue;
                }
                System.out.println("반납자 ID을 입력해주세요");
                System.out.print("입력 : ");
                String studentId = scanner.nextLine();
                if (studentId.equals("0")) {
                    return;
                }
                if (studentId == null || studentId.trim().isEmpty()) {
                    System.out.println("반납자 아이디을 적어주세요");
                    continue;
                }
                int bookId;
                int studId;
                try {
                    bookId = Integer.parseInt(id);
                    studId = Integer.parseInt(studentId);
                } catch (NumberFormatException e) {
                    System.out.println("숫자만 입력해주세요");
                    continue;
                }
                try {
                    int rows = libraryService.returnBook(bookId,studId);
                   if (rows <= 0 ) {
                       System.out.println("반납실패");
                       continue;
                   }else {
                       System.out.println("반납성공");
                   }

                   return;
                } catch (SQLException e) {
                    System.out.println("일시적인 오류로 반납을 처리하지 못했습니다. 잠시 후 다시 시도해주세요");
                    System.out.println("[디버그] " + e.getMessage());
                    continue;
                }

            }
        }

}

package com.tenco.service;

// 비즈니스 로직을 처리하는 클래스
// 호출의 흐름
// View 사용자의 입력 -> service 규칙검사 -> DB 요청과 응답

import com.tenco.dao.AdminDAO;
import com.tenco.dao.BookDAO;
import com.tenco.dao.BorrowDAO;
import com.tenco.dao.StudentDAO;
import com.tenco.dto.Admin;
import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import lombok.SneakyThrows;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class LibraryService {
    // 인터페이스 먼저 설계 - 이 단원에서는 생략 x

    //service를 dao에 맞게 여러개로해도된다
    // service 하나가 dao 세개를 소유한다
    private  final  BookDAO bookDAO = new BookDAO();
    private  final BorrowDAO borrowDAO = new BorrowDAO();
    private  final StudentDAO studentDAO = new StudentDAO();
    private  final AdminDAO adminDAO = new AdminDAO();

    // 도서 추가기능
    // 1. 제목 , 저자가 비어 있는지 확인 둘중 하나라도없으면 중단
    //  2. 통과하면 dao 에ㅔ insert 처리 위임
    //
    public void addBook(Book book) throws SQLException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
            book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 제목과 저자는 필수 입력 항목입니다");
        }
        // 위임처리
        bookDAO.addBook(book);
    }

//    2. 전체 도서 조회
    // 검사할 규칙이 없으므로 dao의 결과를 그대로 넘깁니다
    public List<Book> getAllBooks () throws SQLException {
        return bookDAO.getAllBook();
    }

//   3. 제목으로 도서조회
    //  제목 null인지  비었는지 확인
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("제목을 작성해주세요");
        }
        return bookDAO.getBookByTitle(title);
    }

    //4. 학생 등록
    // 이름 학번 필수  비어있는지확인
    // 통과하면 dao에 insert 위임
    // 추가로 유니크 걸려있는 student_id 는 DB에서 확인해야함으로 여기서는 먼저 중복검사를 안할예정
    // 실무에서는 여기서확인하느게 맞음
    public void addStudent (Student student) throws SQLException {
        if (student.getName() == null || student.getName().trim().isEmpty() ||
            student.getStudentId() == null || student.getStudentId().trim().isEmpty()){
            throw new SQLException("이름 과 학번은 필수 입력 항목입니다");
        }
        studentDAO.addStudent(student);
    }

    //5. 전체 학생 조회
    public List<Student> getAllStudent () throws SQLException {
        return studentDAO.getAllStudent();
    }

    // 6.로그인?(학번으로 학생찾기)
    // 1. 학번 비어있는지 검사
    // 2. dao에서 해당 학번을 찾는다
    // 3. 찾으면 student을 , 없으면 null을 그대로 view에 돌려준다
    //여기 코드에서는 비밀번호없이 학번만 맞으면 로그인되는것으로 단순화 처리
    public Student getStudentByStudentId (String studentId) throws SQLException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new SQLException("학번을 입력해주세요");
        }
        return studentDAO.getStudentByStudentId(studentId);
    }

    //7. 도서대출
    // 1.도서 아이디랑 ,학생 아이디가 1이상인지 검사 (auto increment는 1부터시작)
    //  2.통과하면 dao 에 트랙젠셕 에위임한다
    // 3. 사실 뷰단에서 먼저 로그인 여부를 확인하고 수행할수있도록 처리가 된다.
    public int borrowBook(int bookId, int studentId) throws Exception {
        if ( bookId <= 0 || studentId <= 0) {
            throw  new SQLException("유효한 도서 ID와 유효한 학생 ID를 입력해주세요");
        }
       return borrowDAO.borrowBook(bookId,studentId);
    }

    //8. 대출중인 도서 조회
    public  List<Borrow> getBorrowedBook () throws Exception {
        return borrowDAO.getBorrowedBooks();
    }
    //.9 도서 반납 기능
    // 도서 번호, 학생 번호
    public int returnBook(int bookId, int studentId) throws Exception {
        if (bookId <= 0 || studentId <= 0) {
            throw  new SQLException("유효한 도서 ID 와 유효한 학생 ID를 입력해주세요");
        }
        return borrowDAO.returnBook(bookId,studentId);
    }

    // 관리자 로그인 (id 와 비밀번호)
  //1. 아이디와 비밀번호가 비어있는지 검사
    //2. DAO에게 해당 아이디에 관리자 정보를 찾는다 (없으면 null)
    // 3. 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호를 비교한다
    // 4. 일치하면 비밀번호를 지운 Admin 객체를 반환 아니면 null을 반환
    public Admin authenticateAdmin(String admin_id, String password) throws SQLException {
        if (admin_id == null || admin_id.trim().isEmpty() ||
            password ==null || password.trim().isEmpty()) {
            throw new SQLException("관리자 ID와 비밀번호를 입력해주세요");
        }
        Admin admin = adminDAO.findByAdminId(admin_id);

//        입력한 비밀번호를 DB의 해쉬값과 비교
//        checkpw가 해시값 앞 부분에서 솔트와 비용을 읽어 같은조건으로 다시 계산한뒤 비교처리합니다
        if (!BCrypt.checkpw(password,admin.getPassword())) {
            return null;
        }
        // 4. 인증이 끝난 객체에 비밀번호를 남겨둘 이유가없으모 지우고 돌려준다
        admin.setPassword(null);
            return admin;
    }

    // 관리자등록
    public void registerAdmin(String adminId,String password, String name) throws SQLException {
        if (adminId == null|| adminId.trim().isEmpty()||
        password == null || password.trim().isEmpty()||
        name == null || name.trim().isEmpty()) {
            throw  new SQLException("관리자 ID, 비밀번호, 이름은 필수 입력 항목입니다.");
        }

        // 솔트 10, 해쉬처리
        String hashed = BCrypt.hashpw(password,BCrypt.gensalt(10));
        Admin admin = Admin.builder()
                .adminId(adminId.trim())
                .password(hashed)
                .name(name.trim())
                .build();
        adminDAO.addAdmin(admin);

    }

    public static void main(String[] args) throws SQLException {
        LibraryService libraryService = new LibraryService();
        libraryService.registerAdmin("admin10","1234","김관리");
    }


}

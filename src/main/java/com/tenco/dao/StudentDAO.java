package com.tenco.dao;

import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    //TODO - 추후 사용하는 측 확인해서 라턴 타입결정
    // 학생 등록 기능
    private int addStudent(Student student) {
        int rows =0;
        String sql = """
                insert into students (name, student_id) 
                 values (?,?);
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());

                rows = pstmt.executeUpdate();
                System.out.println( rows+"행이 추가되었습니다");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }



    //학생 전체조회
    public List<Student> getAllStudent () {
        List<Student> studentList = new ArrayList<>();
        String sql = """
            select  * from students
""";
        // select * from students;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();) {

           while (rs.next()) {
               // 아이디 , 이름 ,학번
               Student student = createStudent(rs);
               studentList.add(student);
           }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return studentList;
    }
    //학생 학번 조회 --> 로그인
    public Student getStudentByStudentId (String studentId) {
        String sql = """
                select * from students
                where student_id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);)
              {
            pstmt.setString(1,studentId);
                  try (ResultSet rs = pstmt.executeQuery()) {

                      if ( !rs.next()){
                          return null;
                      }
                      return createStudent(rs);
                  }
              } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Student createStudent (ResultSet rs) throws SQLException {
        return new Student(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("student_id"));
    }



}

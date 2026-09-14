package com.tenco.dao;


import com.tenco.Main;
import com.tenco.dto.Admin;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 관리자 관련 sql을 살행하는 DAO 클래스
public class AdminDAO {

    //  관리자 ID 로 관리자 한명을 조회
    // 비밀번호는 sql에서 비교 하지않고 DB에 저장된값을 그대로 가져오게한다
    // 입력한 '비밀번호가 맞는가'는 업무규칙이므로  Service에게 판단을 시킬 예정
    // 이렇게하면 추후 나중에 비밀번호 암호화(해시처리) 바꿀떄 크데 변경할 부분이없어진다
    public Admin findByAdminId(String admin_id) {
        String sql = """
                select id,admin_id,password,name from admins where admin_id = ?;
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmtm = conn.prepareStatement(sql);) {
             psmtm.setString(1, admin_id);

            try (ResultSet rs = psmtm.executeQuery()) {
                if (rs.next()){
                return   Admin.builder()
                        .id(rs.getInt("id"))
                        .adminId(rs.getNString("admin_id"))
                        .password(rs.getString("password"))
                        .name(rs.getString("name"))
                        .build();
            }}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    // 관리자 등록기능 추가
    public void addAdmin(Admin admin) throws SQLException {
        String sql = """
                insert into admins(admin_id, password, name)
                values (?,?,?)
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1,admin.getAdminId());
            pstmt.setString(2,admin.getPassword());
            pstmt.setString(3,admin.getName());

           int row = pstmt.executeUpdate();
        }

    }

    public static void main(String[] args) throws SQLException {
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = Admin.builder()
                .adminId("admin5")
                .name("티모관리자")
                .password("123")
                .build();

        adminDAO.addAdmin(admin);
    }

}

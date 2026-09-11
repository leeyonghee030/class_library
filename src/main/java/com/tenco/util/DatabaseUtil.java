package com.tenco.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//코드수정 - [hikariCP] 커넥션 풀을 관리하는 클래스
public class DatabaseUtil {

    private  static final HikariDataSource dataSource;
    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Seoul";
    private static final String  DB_USER= System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    //static 블록은 이클래스가 처음 사용되는 순간 딱 한번 실행됩니다
    // 즉 첫 getConnection() 호출 시점에서풀이 만들어 지고 그뒤로는 재사용 합니다
    static {
        HikariConfig config = new HikariConfig();

        //1. 기본 연결 정보 설정
        config.setJdbcUrl(URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        //2.커넥션 풀 크기 설정
        config.setMaximumPoolSize(10); //동시에 최대 10개 유지 (스프링부트 기본값)
        config.setMinimumIdle(5); //요청이 없어도 최소 5개 준비 상태로 유지

        //3. 풀이 가득 찼을떄 빈 연결을 기다리는 최대시간 밀리초
        config.setConnectionTimeout(3000);

        //4.  풀생성 이시점에서 실제 DB 연결객체 n개가 만들어진다
        dataSource = new HikariDataSource(config);
    }
    // DB 접근
    // 주소 계정 패스워드 필요



    public static Connection getConnection () throws SQLException {
        return dataSource.getConnection();

//        System.out.println(connection.getMetaData().getDatabaseProductName());
//        System.out.println(connection.getMetaData().getDatabaseProductVersion());
    }
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()){
            dataSource.close();
        }
    }
}

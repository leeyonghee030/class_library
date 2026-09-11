package com.tenco.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // 출력할떄 password 뻄
public class Admin {

    //id int primary key auto_increment,
    //    admin_id varchar(50) not null unique,
    //    password varchar(255) not null,
    //    name varchar(100) not null

    private int id;
    private String adminId;
    private String password;
    private String name;

    public Admin(String adminId, String password, String name) {
        this.adminId = adminId;
        this.password = password;
        this.name = name;
    }
}

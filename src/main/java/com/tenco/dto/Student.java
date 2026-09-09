package com.tenco.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    private String name;
    private String studentId;
}

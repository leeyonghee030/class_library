package com.tenco.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.prefs.Preferences;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {
    private int id;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    private String name;
    private String studentId;


}

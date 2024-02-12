package com.capstone.uniculture.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    String filePath;

    public Files(String filename, String filePath) {
        this.filename = filename;
        this.filePath = filePath;
    }
}
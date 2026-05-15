package com.email.writer.email.writer.app;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EmailHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tone;

    @Column(length = 5000)
    private String emailContent;

    @Column(length = 5000)
    private String generatedReply;
}
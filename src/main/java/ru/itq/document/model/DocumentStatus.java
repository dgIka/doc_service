package ru.itq.document.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_status")
public class DocumentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StatusCode code;
}


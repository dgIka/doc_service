package ru.itq.document.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_action")
public class DocumentAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ActionCode code;
}


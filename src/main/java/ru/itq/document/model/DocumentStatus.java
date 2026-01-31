package ru.itq.document.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.itq.document.model.enums.StatusCode;

@Entity
@Table(name = "document_status")
@Getter
@Setter
public class DocumentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    private StatusCode code;
}


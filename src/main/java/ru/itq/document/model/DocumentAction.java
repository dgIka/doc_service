package ru.itq.document.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.itq.document.model.enums.ActionCode;

@Entity
@Table(name = "document_action")
@Getter
@Setter
public class DocumentAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    private ActionCode code;
}


package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.document.model.Document;

import java.util.Collection;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByIdIn(Collection<Long> ids);
}


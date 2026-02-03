package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itq.document.model.Document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    List<Document> findByIdIn(Collection<Long> ids);

}


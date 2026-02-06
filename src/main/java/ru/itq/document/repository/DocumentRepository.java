package ru.itq.document.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.itq.document.model.Document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    List<Document> findByIdIn(Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Document d where d.id = :id")
    Optional<Document> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "status",
            "history",
            "history.action"
    })
    @Query("select d from Document d where d.id = :id")
    Optional<Document> findByIdWithHistory(@Param("id") Long id);

}


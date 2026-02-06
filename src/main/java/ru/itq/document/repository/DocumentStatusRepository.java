package ru.itq.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itq.document.model.DocumentStatus;
import ru.itq.document.model.enums.StatusCode;

import java.util.Optional;

public interface DocumentStatusRepository
        extends JpaRepository<DocumentStatus, Long> {

    Optional<DocumentStatus> findByCode(StatusCode code);

    @Query("""
select s.code
from Document d
join d.status s
where d.id = :id
""")
    StatusCode findStatusCodeById(@Param("id") Long id);
}


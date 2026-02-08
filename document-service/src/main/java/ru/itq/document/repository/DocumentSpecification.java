package ru.itq.document.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.itq.document.model.Document;
import ru.itq.document.model.enums.StatusCode;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> search(
            StatusCode status,
            String author,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status").get("code"), status)
                );
            }
            if (author != null && !author.isBlank()) {
                predicates.add(
                        cb.equal(root.get("author"), author)
                );
            }
            if (dateFrom != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom)
                );
            }
            if (dateTo != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createdAt"), dateTo)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

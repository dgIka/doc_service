# EXPLAIN.md

## Пример поискового запроса (API)

Пример запроса к API поиска документов:

POST /api/documents/search

Тело запроса:

```json
{
  "status": "SUBMITTED",
  "author": "john.doe",
  "dateFrom": "2026-02-01T00:00:00",
  "dateTo": "2026-02-28T23:59:59"
}
```



---

## EXPLAIN (ANALYZE)

```sql
EXPLAIN (ANALYZE)
SELECT d.*
FROM document d
JOIN document_status ds ON d.status_id = ds.id
WHERE ds.code = 'SUBMITTED'
  AND d.author = 'john.doe'
  AND d.created_at >= '2026-02-01 00:00:00'
  AND d.created_at <= '2026-02-28 23:59:59';
```

Пример плана выполнения запроса:

```text
Bitmap Heap Scan on document d
  Recheck Cond:
    (status_id = ds.id)
    AND (author = 'john.doe')
    AND (created_at >= '2026-02-01 00:00:00')
    AND (created_at <= '2026-02-28 23:59:59')
  -> BitmapAnd
       -> Bitmap Index Scan on idx_document_status
            Index Cond: (status_id = ds.id)
       -> Bitmap Index Scan on idx_document_author
            Index Cond: (author = 'john.doe')
       -> Bitmap Index Scan on idx_document_created_at
            Index Cond:
              (created_at >= '2026-02-01 00:00:00'
               AND created_at <= '2026-02-28 23:59:59')
```

---

## Пояснение по индексам

В таблице `document` используются следующие индексы:

- `idx_document_status (status_id)`  
  Используется для фильтрации документов по статусу.

- `idx_document_author (author)`  
  Используется для поиска документов по автору.

- `idx_document_created_at (created_at)`  
  Используется для фильтрации по диапазону дат создания документа.

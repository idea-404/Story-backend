package org.example.story.domain.main.repository;

import java.util.List;

public interface GenericCursorRepository<T> {
    List<T> findWithCursor(
            Long lastId,
            int size,
            String sortBy,
            boolean desc,
            String keyword,
            Boolean zerodog
    );
}

package org.example.story.domain.main.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class GenericCursorRepositoryCustom<T> implements GenericCursorRepository<T> {

    @PersistenceContext
    private EntityManager em;

    private final Class<T> domainClass;

    public GenericCursorRepositoryCustom(final Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public List<T> findWithCursor(Long lastId, int size, String sortBy,
                                  boolean desc, String keyword, Boolean zerodog) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(domainClass);
        Root<T> root = cq.from(domainClass);

        List<Predicate> predicates = new ArrayList<>();

        /* ------------------------------
         *   User Fetch Join + Join
         * ------------------------------ */
        Join<Object, Object> userJoin = null;
        boolean hasUserRelation = true;

        try {
            // nickname 조회를 위한 fetch join (항상 즉시 로딩 → N+1 제거)
            root.fetch("user", JoinType.LEFT);

            // keyword 검색을 위해 일반 join도 필요
            userJoin = root.join("user", JoinType.LEFT);
        } catch (IllegalArgumentException e) {
            hasUserRelation = false;
        }


        /* ------------------------------
         *     Cursor 페이징 조건
         * ------------------------------ */
        if (lastId != null) {
            predicates.add(cb.lessThan(root.get("id"), lastId));
        }

        /* ------------------------------
         *     zerodog 필터 (Portfolio 전용)
         * ------------------------------ */
        try {
            root.get("zerodog");
            if (zerodog != null) {
                predicates.add(cb.equal(root.get("zerodog"), zerodog));
            }
        } catch (IllegalArgumentException e) {
            log.debug("Entity {} does not have field 'zerodog', skipping filter",
                    domainClass.getSimpleName());
        }


        /* ------------------------------
         *     Keyword 검색 (title/content/nickname)
         * ------------------------------ */
        Expression<Integer> matchCount = cb.literal(0);

        if (keyword != null && !keyword.isEmpty()) {

            Expression<Integer> titleMatch;
            Expression<Integer> contentMatch;
            Expression<Integer> nicknameMatch = cb.literal(0);

            try {
                titleMatch = cb.<Integer>selectCase()
                        .when(cb.like(root.get("title"), "%" + keyword + "%"), 1)
                        .otherwise(0);

                contentMatch = cb.<Integer>selectCase()
                        .when(cb.like(root.get("content"), "%" + keyword + "%"), 1)
                        .otherwise(0);

                if (hasUserRelation) {
                    nicknameMatch = cb.<Integer>selectCase()
                            .when(cb.like(userJoin.get("nickname"), "%" + keyword + "%"), 1)
                            .otherwise(0);
                }

            } catch (IllegalArgumentException e) {
                log.debug("Entity {} does not support title/content search, skipping keyword match",
                        domainClass.getSimpleName());
                titleMatch = cb.literal(0);
                contentMatch = cb.literal(0);
            }

            matchCount = cb.sum(cb.sum(titleMatch, contentMatch), nicknameMatch);

            predicates.add(cb.greaterThan(matchCount, 0));

            // 검색 시: matchCount DESC 우선 정렬
            cq.orderBy(cb.desc(matchCount), cb.desc(root.get("like")));

        } else {
            /* ------------------------------
             *    기본 정렬 (검색 없을 때)
             * ------------------------------ */
            if ("id".equals(sortBy)) {
                // id 단독 정렬
                if (desc) cq.orderBy(cb.desc(root.get("id")));
                else cq.orderBy(cb.asc(root.get("id")));
            } else {
                Path<?> sortPath = root.get(sortBy);
                if (desc) cq.orderBy(cb.desc(sortPath), cb.desc(root.get("id")));
                else cq.orderBy(cb.asc(sortPath), cb.desc(root.get("id")));
            }
        }


        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<T> query = em.createQuery(cq);
        query.setMaxResults(size);

        return query.getResultList();
    }

}

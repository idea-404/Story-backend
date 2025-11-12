package org.example.story.domain.main.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenericCursorRepositoryImpl<T> implements GenericCursorRepository<T> {

    @PersistenceContext
    private EntityManager em;

    private final Class<T> domainClass;

    public GenericCursorRepositoryImpl(final Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public List<T> findWithCursor(Long lastId, int size, String sortBy,
                                  boolean desc, String keyword, Boolean zerodog){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(domainClass);
        Root<T> root = cq.from(domainClass);

        List<Predicate> predicates = new ArrayList<Predicate>();

        // User 테이블 nickname 검색용
        Join<Object, Object> userJoin = null;
        try {
            userJoin = root.join("user", JoinType.LEFT);
        } catch (Exception e) {
            // user 관계 없는 엔티티는 무시
        }

        // 커서 페이징 조건
        if(lastId != null){
            predicates.add(cb.lessThan(root.get("id"), lastId));
        }

        // 포트폴리오에서만 사용 블로그에선 값을 누락시켜 넘김
        try{
            root.get("zerodog");
            if(zerodog != null){
                predicates.add(cb.equal(root.get("zerodog"), zerodog));
            }
        }catch(IllegalArgumentException e){}

        Expression<Integer> matchCount = cb.literal(0);

        if (keyword != null && !keyword.isEmpty()) {
            Expression<Integer> titleMatch;
            Expression<Integer> contentMatch;
            Expression<Integer> nicknameMatch = cb.literal(0);

            try {
                // CASE WHEN title LIKE '%keyword%' THEN 1 ELSE 0 END
                titleMatch = cb.<Integer>selectCase()
                        .when(cb.like(root.get("title"), "%" + keyword + "%"), 1)
                        .otherwise(0);

                // CASE WHEN content LIKE '%keyword%' THEN 1 ELSE 0 END
                contentMatch = cb.<Integer>selectCase()
                        .when(cb.like(root.get("content"), "%" + keyword + "%"), 1)
                        .otherwise(0);

                // CASE WHEN nickname LIKE '%keyword%' THEN 1 ELSE 0 END (user가 있을 경우만)
                if (userJoin != null) {
                    nicknameMatch = cb.<Integer>selectCase()
                            .when(cb.like(userJoin.get("nickname"), "%" + keyword + "%"), 1)
                            .otherwise(0);
                }

            } catch (Exception e) {
                // 기본 안전값으로 초기화
                titleMatch = cb.literal(0);
                contentMatch = cb.literal(0);
                nicknameMatch = cb.literal(0);
            }

            // matchCount = titleMatch + contentMatch + nicknameMatch
            matchCount = cb.sum(cb.sum(titleMatch, contentMatch), nicknameMatch);

            // 매치된 항목만 (즉, 하나라도 일치한 경우)
            predicates.add(cb.greaterThan(matchCount, 0));

            // ORDER BY matchCount DESC, like DESC
            cq.orderBy(cb.desc(matchCount), cb.desc(root.get("like")));
        } else {
            // keyword 없을 시 기본 정렬
            Path<?> sortPath = root.get(sortBy);
            if (desc) cq.orderBy(cb.desc(sortPath), cb.desc(root.get("id")));
            else cq.orderBy(cb.asc(sortPath), cb.desc(root.get("id")));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<T> query = em.createQuery(cq);
        query.setMaxResults(size);

        return query.getResultList();
    }
}

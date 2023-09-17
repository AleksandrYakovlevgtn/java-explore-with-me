package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.StatsDtoRequest;
import ru.practicum.stats.dto.StatsDtoResponse;
import ru.practicum.stats.server.model.StatsMapper;
import ru.practicum.stats.server.model.Stats;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EntityManager entityManager;
    private final StatsMapper statsMapper;

    @Override
    @Transactional(readOnly = true)
    public void add(StatsDtoRequest statsDtoRequest) {
        entityManager.persist(statsMapper.toEntity(statsDtoRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDtoResponse> search(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatsDtoResponse> cr = cb.createQuery(StatsDtoResponse.class);
        Root<Stats> root = cr.from(Stats.class);
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(cb.greaterThanOrEqualTo(root.get("timestamp"), start));
        conditions.add(cb.lessThan(root.get("timestamp"), end));
        if (!Objects.isNull(uris) && !uris.isEmpty()) {
            conditions.add(cb.in(root.get("uri")).value(uris));
        }
        Expression<Long> count = unique
                ? cb.countDistinct(root.get("ip"))
                : cb.count(root.get("ip"));
        cr.multiselect(root.get("app"), root.get("uri"), count)
                .where(conditions.toArray(new Predicate[]{}))
                .groupBy(root.get("app"), root.get("uri"))
                .orderBy(cb.desc(count));
        return entityManager.createQuery(cr).getResultList();

    }
}
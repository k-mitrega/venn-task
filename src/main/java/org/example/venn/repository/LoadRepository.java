package org.example.venn.repository;

import jakarta.persistence.EntityManager;
import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.Load;
import org.example.venn.repository.entity.LoadEntity;
import org.example.venn.repository.mapper.LoadMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public class LoadRepository {

    private final EntityManager entityManager;
    private final LoadMapper mapper;

    public LoadRepository(EntityManager entityManager, LoadMapper mapper) {
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Transactional
    public Load save(Load load) {
        final var entity = mapper.toEntity(load);
        entityManager.persist(entity);
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public List<Load> findByCustomerIdAndTimeRange(
            CustomerId customerId, Instant startInclusive, Instant endExclusive) {
        return entityManager.createQuery(
                        "select l from LoadEntity l "
                                + "where l.customerId = :customerId "
                                + "and l.time >= :startInclusive and l.time < :endExclusive",
                        LoadEntity.class)
                .setParameter("customerId", customerId.value())
                .setParameter("startInclusive", startInclusive)
                .setParameter("endExclusive", endExclusive)
                .getResultList()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}

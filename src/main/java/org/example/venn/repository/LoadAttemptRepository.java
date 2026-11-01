package org.example.venn.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.domain.model.LoadId;
import org.example.venn.repository.entity.LoadAttemptEntity;
import org.example.venn.repository.mapper.LoadAttemptMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class LoadAttemptRepository {

    private static final String DUPLICATE_ATTEMPT_CONSTRAINT =
            "UK_LOAD_ATTEMPTS_CUSTOMER_EXTERNAL_ID";

    private final EntityManager entityManager;
    private final LoadAttemptMapper mapper;

    public LoadAttemptRepository(EntityManager entityManager, LoadAttemptMapper mapper) {
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Optional<LoadAttempt> findByCustomerIdAndLoadId(CustomerId customerId, LoadId loadId) {
        return entityManager.createQuery(
                        "select a from LoadAttemptEntity a "
                                + "where a.customerId = :customerId and a.externalId = :loadId",
                        LoadAttemptEntity.class)
                .setParameter("customerId", customerId.value())
                .setParameter("loadId", loadId.value())
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Transactional
    public LoadAttempt save(LoadAttempt loadAttempt) {
        final var entity = mapper.toEntity(loadAttempt);
        entityManager.persist(entity);
        try {
            entityManager.flush();
        } catch (PersistenceException e) {
            if (ConstraintViolations.isUniqueViolation(e, DUPLICATE_ATTEMPT_CONSTRAINT)) {
                throw new DuplicateLoadAttemptException(e);
            }
            throw e;
        }
        return mapper.toDomain(entity);
    }
}

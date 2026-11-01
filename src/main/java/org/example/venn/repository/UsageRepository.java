package org.example.venn.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.Usage;
import org.example.venn.repository.entity.UsageEntity;
import org.example.venn.repository.mapper.UsageMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UsageRepository {

    private static final String DUPLICATE_CUSTOMER_CONSTRAINT =
            "UK_CUSTOMER_USAGE_CUSTOMER_ID";

    private final EntityManager entityManager;
    private final UsageMapper mapper;

    public UsageRepository(EntityManager entityManager, UsageMapper mapper) {
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Optional<Usage> findByCustomerId(CustomerId customerId) {
        return entityManager.createQuery(
                        "select u from UsageEntity u where u.customerId = :customerId",
                        UsageEntity.class)
                .setParameter("customerId", customerId.value())
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Usage> findByCustomerIdForUpdate(CustomerId customerId) {
        return entityManager.createQuery(
                        "select u from UsageEntity u where u.customerId = :customerId",
                        UsageEntity.class)
                .setParameter("customerId", customerId.value())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    @Transactional
    public Usage save(Usage usage) {
        final var entity = entityManager.merge(mapper.toEntity(usage));
        try {
            entityManager.flush();
        } catch (PersistenceException e) {
            if (ConstraintViolations.isUniqueViolation(e, DUPLICATE_CUSTOMER_CONSTRAINT)) {
                throw new ConcurrentUsageCreationException(e);
            }
            throw e;
        }
        return mapper.toDomain(entity);
    }
}

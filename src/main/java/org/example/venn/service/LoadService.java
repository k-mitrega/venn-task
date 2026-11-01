package org.example.venn.service;

import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.repository.ConcurrentUsageCreationException;
import org.example.venn.repository.DuplicateLoadAttemptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadService {

    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);

    private final LoadProcessor loadProcessor;

    LoadService(LoadProcessor loadProcessor) {
        this.loadProcessor = loadProcessor;
    }

    public LoadResult load(LoadAttempt loadAttempt) {
        final LoadDecision decision;
        try {
            decision = loadProcessor.process(loadAttempt);
        } catch (DuplicateLoadAttemptException e) {
            logger.info("load attempt {} for customer {}: duplicate (lost race)",
                    loadAttempt.loadId().value(), loadAttempt.customerId().value());
            throw new DuplicateLoadConflictException(e);
        } catch (ConcurrentUsageCreationException e) {
            logger.warn("load attempt {} for customer {}: concurrent usage creation conflict",
                    loadAttempt.loadId().value(), loadAttempt.customerId().value());
            throw new ConcurrentLoadConflictException(e);
        }
        if (decision == LoadDecision.DUPLICATE) {
            logger.info("load attempt {} for customer {}: duplicate",
                    loadAttempt.loadId().value(), loadAttempt.customerId().value());
            throw new DuplicateLoadConflictException();
        }
        logger.info("load attempt {} for customer {}: {}",
                loadAttempt.loadId().value(), loadAttempt.customerId().value(), decision);
        return new LoadResult(loadAttempt, decision);
    }

    public List<LoadResult> process(List<LoadAttempt> loadAttempts) {
        return loadAttempts.stream().map(this::loadWithRetry).toList();
    }

    private LoadResult loadWithRetry(LoadAttempt loadAttempt) {
        try {
            return load(loadAttempt);
        } catch (DuplicateLoadConflictException e) {
            return new LoadResult(loadAttempt, LoadDecision.DUPLICATE);
        } catch (ConcurrentLoadConflictException e) {
            logger.info("retrying load attempt {} after concurrent conflict",
                    loadAttempt.loadId().value());
            return load(loadAttempt);
        }
    }
}

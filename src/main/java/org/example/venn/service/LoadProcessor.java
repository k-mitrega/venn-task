package org.example.venn.service;

import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.repository.LoadAttemptRepository;
import org.example.venn.repository.LoadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class LoadProcessor {

    private final LoadAttemptRepository loadAttemptRepository;
    private final LoadRepository loadRepository;
    private final UsageService usageService;

    LoadProcessor(
            LoadAttemptRepository loadAttemptRepository,
            LoadRepository loadRepository,
            UsageService usageService) {
        this.loadAttemptRepository = loadAttemptRepository;
        this.loadRepository = loadRepository;
        this.usageService = usageService;
    }

    @Transactional
    public LoadDecision process(LoadAttempt loadAttempt) {
        if (loadAttemptRepository.findByCustomerIdAndLoadId(
                loadAttempt.customerId(), loadAttempt.loadId()).isPresent()) {
            return LoadDecision.DUPLICATE;
        }
        loadAttemptRepository.save(loadAttempt);

        final var load = loadAttempt.toLoad();
        final var usage = usageService.usageFor(load);
        if (!usageService.usageInLimit(usage)) {
            return LoadDecision.DECLINED;
        }

        loadRepository.save(load);
        usageService.save(usage);
        return LoadDecision.ACCEPTED;
    }
}

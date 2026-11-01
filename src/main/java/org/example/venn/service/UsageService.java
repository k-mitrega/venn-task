package org.example.venn.service;

import org.example.venn.config.UsageLimits;
import org.example.venn.domain.model.Load;
import org.example.venn.domain.model.Usage;
import org.example.venn.repository.UsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageService {

    private final UsageRepository usageRepository;
    private final UsageLimits usageLimits;

    public UsageService(UsageRepository usageRepository, UsageLimits usageLimits) {
        this.usageRepository = usageRepository;
        this.usageLimits = usageLimits;
    }

    @Transactional
    public Usage usageFor(Load load) {
        return usageRepository.findByCustomerIdForUpdate(load.customerId())
                .map(usage -> usage.updateUsage(load))
                .orElseGet(() -> Usage.newUsageFromLoad(load));
    }

    public boolean usageInLimit(Usage usage) {
        return usage.dailyAmountUsed().compareTo(usageLimits.dailyLoadAmountLimit()) <= 0
                && usage.weeklyAmountUsed().compareTo(usageLimits.weeklyLoadAmountLimit()) <= 0
                && usage.dailyLoadsUsed() <= usageLimits.dailyLoadCountLimit();
    }

    @Transactional
    public Usage save(Usage usage) {
        return usageRepository.save(usage);
    }
}

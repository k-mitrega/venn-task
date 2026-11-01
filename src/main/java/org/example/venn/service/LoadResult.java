package org.example.venn.service;

import org.example.venn.domain.model.LoadAttempt;

public record LoadResult(LoadAttempt attempt, LoadDecision decision) {
}

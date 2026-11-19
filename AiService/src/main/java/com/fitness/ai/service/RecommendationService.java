package com.fitness.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fitness.ai.model.Recommendation;
import com.fitness.ai.repository.RecommendationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
	private final RecommendationRepository repo;

	public List<Recommendation> getUserRecommendatin(String userId) {
		return repo.findByUserId(userId);
	}

	public Recommendation getActivityRecommendatin(String activityId) {
		return repo.findByActivityId(activityId)
		        .orElseThrow(() -> new RuntimeException("No recommendation found for activity ID: " + activityId));	}

}

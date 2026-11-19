package com.fitness.ai.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fitness.ai.model.Activity;
import com.fitness.ai.model.Recommendation;
import com.fitness.ai.repository.RecommendationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListner {

	private final ActivityAi ai;
	private final RecommendationRepository repo;

	@RabbitListener(queues = "activity.queue")
	public void processActivity(Activity activity) {
		log.info("Received Activity for Processing: {}", activity.getId());

		try {
			Recommendation recommendation = ai.generateRecommendation(activity);
			if (recommendation != null) {
				repo.save(recommendation);
				log.info("Saved recommendation for activity: {}", activity.getId());
			} else {
				log.warn("Skipped saving null recommendation for activity: {}", activity.getId());
			}
		} catch (WebClientResponseException.TooManyRequests e) {
			log.error("Gemini quota exceeded (429) while processing activity {}. Skipping requeue loop.", activity.getId());
		} catch (Exception e) {
			log.error("Failed to process activity {}: {}", activity.getId(), e.getMessage(), e);
		}
	}
}
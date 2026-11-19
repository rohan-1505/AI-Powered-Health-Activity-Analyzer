package com.fitness.ai.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

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
	
	
	
	
	@RabbitListener(queues="activity.queue")
	
	public void processActivity(Activity activity) {
		log.info("Recieved Activity for Processing: {}", activity.getId());
//		log.info("Generated recommendation: {}", ai.generateRecommendation(activity));
		Recommendation recommendation = ai.generateRecommendation(activity);
		repo.save(recommendation);
	}

}

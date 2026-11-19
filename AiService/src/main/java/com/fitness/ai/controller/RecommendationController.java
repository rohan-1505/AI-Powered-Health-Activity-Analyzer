package com.fitness.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.ai.model.Recommendation;
import com.fitness.ai.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
	private final RecommendationService service;

	
	@GetMapping("user/{userId}")
	public ResponseEntity<java.util.List<Recommendation>> getUserRecommendation(@PathVariable String userId){
		return ResponseEntity.ok(service.getUserRecommendatin(userId));
		
	}
	
	@GetMapping("activity/{activityId}")
	public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId){
		return ResponseEntity.ok(service.getActivityRecommendatin(activityId));
		
	}
	

}

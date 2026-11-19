package com.fitness.ai.model;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class Activity {
    

	private String id;
	private String userId;
	private String type;
	
	private Integer duration;
	private Integer caloriesBurned;
	private LocalDateTime startTime;
	
	private Map<String, Object> additionalMetrics;
	

	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;

}

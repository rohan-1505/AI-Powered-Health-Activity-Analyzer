package com.fitness.userservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.userservice.Repository.ActivityRepository;
import com.fitness.userservice.dto.ActivityRequest;
import com.fitness.userservice.dto.ActivityResponse;
import com.fitness.userservice.model.Activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityService {

	private final ActivityRepository repo;
	private final UserValidationService userservice;
	private final RabbitTemplate rabbittemp;
	
	@Value("${rabbitmq.exchange.name}")
	private String exchange;
	@Value("${rabbitmq.routing.key}")
	private String routingkey;
	
	public ActivityResponse trackactivity(ActivityRequest request) {
		
		boolean isValidUser= userservice.validateUser(request.getUserId());
				if(!isValidUser) {
					throw new RuntimeException("Invalid User"+request.getUserId());
				}
		
		Activity activity = Activity.builder()
				.userId(request.getUserId())
				.type(request.getType())
				.duration(request.getDuration())
				.caloriesBurned(request.getCaloriesBurned())
				.startTime(request.getStartTime())
				.additionalMetrics(request.getAdditionalMetrics())
				.build();
		
		Activity savedActivity = repo.save(activity);
		
		//publish to rabbitmq for ai processing
		try {
		    rabbittemp.convertAndSend(exchange, routingkey, savedActivity);
		    log.info("Activity published successfully to RabbitMQ");
		} catch (Exception e) {
		    log.error("Failed to publish activity to RabbitMQ", e);
		}
		return mapToResponse(savedActivity);
	}
	
	private ActivityResponse mapToResponse(Activity activity) {
		ActivityResponse response = new ActivityResponse();
		response.setId(activity.getId());
		response.setUserId(activity.getUserId());
		response.setType(activity.getType());
		response.setDuration(activity.getDuration());
		response.setStartTime(activity.getStartTime());
		response.setCaloriesBurned(activity.getCaloriesBurned());
		response.setAdditionalMetrics(activity.getAdditionalMetrics());
		
		
		response.setCreatedAt(activity.getCreatedAt());
		response.setUpdatedAt(activity.getUpdatedAt());
		
		return response;
	}

	public List<ActivityResponse> getUserActivity(String userId) {
		List<Activity> activities=	repo.findByUserId(userId);
		return activities.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ActivityResponse getActivityById(String activityId) {
		
		return repo.findById(activityId)
				.map(this::mapToResponse)
				.orElseThrow(()->new RuntimeException("activity not found with id :" + activityId))
				;
	}

}
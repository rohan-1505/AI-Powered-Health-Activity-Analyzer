package com.fitness.userservice.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fitness.userservice.model.Activity;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String>{



	List<Activity> findByUserId(String userId);

}

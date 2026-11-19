package com.fitness.ai.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.ai.model.Activity;
import com.fitness.ai.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAi {
	private final GeminiService geminiservice;
	
	// Updated return type to Recommendation and passed activity parameter
	public Recommendation generateRecommendation(Activity activity) {
		String prompt = createPromptActivity(activity);
		String aiResponse = geminiservice.getAnswer(prompt);
		
		// Pass both aiResponse and activity to processAiResponse
		return processAiResponse(aiResponse, activity);
	}
	
	private Recommendation processAiResponse(String aiResponse, Activity activity) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootnode = mapper.readTree(aiResponse);
				JsonNode textNode = rootnode.path("candidates")
						.get(0)
						.path("content")
						.path("parts")
						.get(0)
						.path("text");
				
				// Clean markdown code blocks safely
				String jsoncontent = textNode.asText()
						.replaceAll("```json\\s*", "")
						.replaceAll("```\\s*$", "")
						.trim();
				
				log.info("PARSED RESPONSE FROM AI: {}", jsoncontent);
				
				// --- PARSING LOGIC ---
				JsonNode analysisJson = mapper.readTree(jsoncontent);
				JsonNode analysisNode = analysisJson.path("analysis");
				StringBuilder fullAnalysis = new StringBuilder();
				addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
				addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
				addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
				addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");
				
				java.util.List<String> improvements = extractImprovements(analysisJson.path("improvements"));
				java.util.List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
				java.util.List<String> safety = extractSafety(analysisJson.path("safety")); 
				
				// Log counts or use parsed lists as needed
				log.info("Extracted Improvements Count: {}", improvements.size());
				log.info("Extracted Suggestions Count: {}", suggestions.size());
				log.info("Extracted Safety Points Count: {}", safety.size());

				return Recommendation.builder()
						.activityId(activity.getId())
						.userId(activity.getUserId())
						.activityType(activity.getType())
						.recommendation(fullAnalysis.toString().trim()) // Note: matches your model's spelling
						.improvements(improvements)
						.suggestions(suggestions)
						.safety(safety)
						.createdAt(LocalDateTime.now())
						.build();
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
	}

	private java.util.List<String> extractSuggestions(JsonNode suggestionNode) {
	    java.util.List<String> suggestions = new ArrayList<>();
	    if (suggestionNode != null && suggestionNode.isArray()) {
	        suggestionNode.forEach(suggestion -> {
	            String workout = suggestion.path("workout").asText();
	            String description = suggestion.path("description").asText();
	            suggestions.add(String.format("%s: %s", workout, description));
	        });
	    }
	    return suggestions.isEmpty() ?
	            Collections.singletonList("No specific suggestions provided") :
	            suggestions;
	}

	private java.util.List<String> extractImprovements(JsonNode improvementNode){
		java.util.List<String> improvements = new ArrayList<>();
		if(improvementNode != null && improvementNode.isArray()) {
			improvementNode.forEach(improvement -> {
				String area = improvement.path("area").asText();
				String detail = improvement.path("recommendation").asText();
				improvements.add(String.format("%s: %s", area, detail));
			});
		}
		return improvements.isEmpty() ?
				Collections.singletonList("No specific improvements provided") :
				improvements;
				
	}

	private java.util.List<String> extractSafety(JsonNode safetyNode) {
	    java.util.List<String> safetyList = new ArrayList<>();
	    if (safetyNode != null && safetyNode.isArray()) {
	        safetyNode.forEach(safetyItem -> {
	            String point = safetyItem.asText();
	            if (!point.isEmpty()) {
	                safetyList.add(point);
	            }
	        });
	    }
	    return safetyList.isEmpty() ?
	            Collections.singletonList("No specific safety points provided") :
	            safetyList;
	}

	private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
		if(!analysisNode.path(key).isMissingNode()) {
			fullAnalysis.append(prefix)
			.append(analysisNode.path(key).asText())
			.append("\n\n");
		}
	}

	private String createPromptActivity(Activity activity) {
		  return String.format("""
			        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
			        {
			          "analysis": {
			            "overall": "Overall analysis here",
			            "pace": "Pace analysis here",
			            "heartRate": "Heart rate analysis here",
			            "caloriesBurned": "Calories analysis here"
			          },
			          "improvements": [
			            {
			              "area": "Area name",
			              "recommendation": "Detailed recommendation"
			            }
			          ],
			          "suggestions": [
			            {
			              "workout": "Workout name",
			              "description": "Detailed workout description"
			            }
			          ],
			          "safety": [
			            "Safety point 1",
			            "Safety point 2"
			          ]
			        }

			        Analyze this activity:
			        Activity Type: %s
			        Duration: %d minutes
			        Calories Burned: %d
			        Additional Metrics: %s
			        
			        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
			        Ensure the response follows the EXACT JSON format shown above.
			        """,
			                activity.getType(),
			                activity.getDuration(),
			                activity.getCaloriesBurned(),
			                activity.getAdditionalMetrics()
			        );
	}
}
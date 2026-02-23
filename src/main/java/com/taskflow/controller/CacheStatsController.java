package com.taskflow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/cache")
public class CacheStatsController {

    private static final Logger logger = LoggerFactory.getLogger(CacheStatsController.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get cache statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        logger.info("GET /api/cache/stats - Fetching cache statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Get all keys
        Set<String> keys = redisTemplate.keys("tasks::*");
        
        if (keys != null) {
            stats.put("totalCachedEntries", keys.size());
            stats.put("cachedKeys", keys);
            
            // Get memory info (requires Redis INFO command)
            try {
                stats.put("message", "Cache is active with " + keys.size() + " entries");
            } catch (Exception e) {
                stats.put("error", "Unable to fetch detailed stats");
            }
        } else {
            stats.put("totalCachedEntries", 0);
            stats.put("message", "No cached entries found");
        }
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Clear all cache
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCache() {
        logger.warn("DELETE /api/cache/clear - Clearing all cache");
        
        Set<String> keys = redisTemplate.keys("tasks::*");
        
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            logger.info("Cleared {} cache entries", keys.size());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache cleared successfully");
            response.put("entriesCleared", String.valueOf(keys.size()));
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No cache entries to clear");
            
            return ResponseEntity.ok(response);
        }
    }
}
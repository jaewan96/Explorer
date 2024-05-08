package com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class MapObjectRepository {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveHashOperations<String, String, String> hashOperations;

    private static final String KEY_PREFIX = "mapData";

    public MapObjectRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.hashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> saveMapData(String channelId, Integer mapId, List<String> positions, Integer category, Integer itemId) {
        Map<String, String> hashData = dataToHash(positions, category, itemId);
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return hashOperations.putAll(key, hashData)
                .map(result -> result == Boolean.TRUE);
    }

    private List<String> selectRandomEntries(List<String> positions, int count) {
        List<String> selectedEntries = new ArrayList<>(positions);
        Collections.shuffle(selectedEntries);  // 리스트를 무작위로 섞음
        return selectedEntries.subList(0, Math.min(selectedEntries.size(), count));  // 랜덤하게 count개의 요소를 선택
    }

    private Map<String, String> dataToHash(List<String> positions, Integer category, Integer itemId) {
        Map<String, String> hashData = new HashMap<>();
        for (String position : positions) {
            hashData.put(position, category + ":" + itemId);
        }
        return hashData;
    }

    public Mono<java.util.Map<String, String>> findMapData(String channelId, Integer mapId) {
        String key = KEY_PREFIX + ":" + channelId + ":" + mapId;
        return hashOperations.entries(key)
                .collectMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue);
    }
}


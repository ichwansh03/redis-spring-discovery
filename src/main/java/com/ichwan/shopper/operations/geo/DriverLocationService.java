package com.ichwan.shopper.operations.geo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLocationService {

    public static final String GEO_KEY = "driver:locations";
    private final StringRedisTemplate redisTemplate;

    public void updateLocation(String driverId, double lat, double lon) {
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(lat, lon), driverId);
    }

    public GeoResults<RedisGeoCommands.GeoLocation<String>> findNearbyDrivers(double lat, double lon, double radius) {
        Circle area = new Circle(
                new Point(lat, lon),
                new Distance(radius, Metrics.KILOMETERS)
        );

        return redisTemplate.opsForGeo().search(GEO_KEY, area);
    }
}

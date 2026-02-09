package com.ichwan.shopper.operations.zsets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    public static final String JOB_QUEUE_KEY = "job:queue";
    private final StringRedisTemplate redisTemplate;

    public void addJob(String jobId, double score) {
        redisTemplate.opsForZSet().add(JOB_QUEUE_KEY, jobId, score);
    }

    public String takeNextJob() {
        Set<String> jobs = redisTemplate.opsForZSet().range(JOB_QUEUE_KEY, 0, 0);

        if (jobs == null || jobs.isEmpty()) return null;

        String jobId = jobs.iterator().next();

        redisTemplate.opsForZSet().remove(JOB_QUEUE_KEY, jobId);

        return jobId;
    }

    public void processJob(String jobId) {
        log.info("processing job: {}", jobId);
    }

    @Scheduled(fixedDelay = 1000)
    public void runWorker() {
        String job = takeNextJob();
        if (job != null) processJob(job);
    }
}

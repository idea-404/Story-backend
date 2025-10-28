package org.example.story.global.aop;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object rateLimits(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getKey(joinPoint);
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(rateLimited));

        if(bucket.tryConsume(1)){
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("요청 제한 초과: 잠시 후 다시 시도해주세요.");
        }
    }

    private Bucket createBucket(RateLimited rateLimited) {
        Refill refill = Refill.intervally(rateLimited.limit(), Duration.ofSeconds(rateLimited.refillDuration()));

        Bandwidth limit = Bandwidth.classic(rateLimited.limit(), refill);

        return Bucket.builder().addLimit(limit).build();
    }

    private String getKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();

        Long userId = null;
        if (args != null && args.length > 0 && args[0] instanceof Long) {
            userId = (Long) args[0];
        }

        if (userId == null) {
            return methodName + ":ANONYMOUS";
        }

        return methodName + ":" + userId;
    }
}

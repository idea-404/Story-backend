package org.example.story.global.aop;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5)) // 5분간 접근 없으면 삭제
            .maximumSize(10_000) // 캐시 최대 크기 제한
            .build();

    @Around("@annotation(rateLimited)")
    public Object rateLimits(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getKey(joinPoint);
        Bucket bucket = buckets.get(key, k -> createBucket(rateLimited));

        if(bucket.tryConsume(1)){
            return joinPoint.proceed();
        } else {
            throw new ExpectedException(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다");
        }
    }

    private Bucket createBucket(RateLimited rateLimited) {
        Refill refill = Refill.intervally(rateLimited.limit(), Duration.ofSeconds(rateLimited.refillDuration()));

        Bandwidth limit = Bandwidth.classic(rateLimited.limit(), refill);

        return Bucket.builder().addLimit(limit).build();
    }

    private String getKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String ip = "UNKNOWN_IP";
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
        }

        Long userId = null;
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Long) {
            userId = (Long) args[0];
        }

        if (userId == null) {
            return methodName + ":ANONYMOUS:" + ip;
        } else {
            return methodName + ":" + userId + ":" + ip;
        }
    }
}

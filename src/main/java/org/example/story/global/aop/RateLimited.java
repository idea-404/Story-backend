package org.example.story.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    int limit() default 5; // 허용 요청 수
    int durationSeconds() default 60; // 기간(초)
    int refillDuration() default 60;
}

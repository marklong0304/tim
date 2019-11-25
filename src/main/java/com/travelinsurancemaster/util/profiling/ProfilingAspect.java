package com.travelinsurancemaster.util.profiling;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Aspect
public class ProfilingAspect {

    private static final Logger log = LoggerFactory.getLogger(ProfilingAspect.class);

    @Value("${profiling.enabled}")
    private boolean enabled;

    @Value("${profiling.execution.min.duration.ms}")
    private long executionMinDurationMs;


    @Pointcut("execution(* com.travelinsurancemaster.services..*.*(..))")
    public void travelPackageService() {
    }

    //TODO fix error handling
    @Pointcut("execution(* com.travelinsurancemaster.web..*.*(..))")
    public void travelPackageWeb() {
    }

    @Around("travelPackageService()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        long startExecutionTime = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            logExecutionTime(pjp, startExecutionTime);
        }
    }

    private void logExecutionTime(ProceedingJoinPoint pjp, long startExecutionTime) {
        if (!enabled) {
            return;
        }
        Logger logger = pjp.getTarget() != null ? LoggerFactory.getLogger(pjp.getTarget().getClass()) : log;
        long durationMs = (System.nanoTime() - startExecutionTime) / 1000000;
        if (durationMs >= executionMinDurationMs) {
            StringBuilder prefix = new StringBuilder();
            prefix.append("------------------------------");
            /*
            for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
                prefix.append("-");
            }
            */
            prefix.append(pjp.getSignature() != null ? pjp.getSignature().getName() : StringUtils.EMPTY)
                    .append(" completed (PROFILING), executed ")
                    .append(durationMs)
                    .append(" ms");
            logger.info(prefix.toString());
        }
    }
}
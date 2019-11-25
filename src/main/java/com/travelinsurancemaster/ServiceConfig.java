package com.travelinsurancemaster;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.util.profiling.ProfilingAspect;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Alexander.Isaenco
 */
@Configuration
public class ServiceConfig implements AsyncConfigurer {

    public static final String THREAD_PREFIX = "ApiClient-";

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Bean
    public ProfilingAspect profilingAspect() {
        return new ProfilingAspect();
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
    }

    @Override
    public AsyncTaskExecutor getAsyncExecutor() {
        if (apiProperties.getMaxPoolSize() == 0) {
            Executor ex = Executors.newCachedThreadPool(new CustomizableThreadFactory(THREAD_PREFIX));
            return new ConcurrentTaskExecutor(ex);
        } else {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(apiProperties.getCorePoolSize());
            executor.setMaxPoolSize(apiProperties.getMaxPoolSize());
            executor.setThreadNamePrefix(THREAD_PREFIX);
            executor.initialize();
            return executor;
        }
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}

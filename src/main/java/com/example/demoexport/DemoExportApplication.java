package com.example.demoexport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableBatchProcessing
public class DemoExportApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoExportApplication.class, args);
    }
}

@Slf4j
@Component
class Init implements ApplicationRunner{

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("myJob")
    Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Init job ");
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
//                .addString("myparameter","this is my super parameter")
                .toJobParameters();
        jobLauncher.run(job, params);
        log.info("Finish job");
    }
}

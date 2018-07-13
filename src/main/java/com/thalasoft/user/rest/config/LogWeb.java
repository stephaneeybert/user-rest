package com.thalasoft.user.rest.config;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;

@Configuration
public class LogWeb {

	private final static String PATTERN = "%date %-5level [%thread] %logger{36} %m%n %rEx";

	@Bean 
    public static LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

	@Bean (initMethod = "start", destroyMethod = "stop")
    public static PatternLayoutEncoder encoder (LoggerContext ctx) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(ctx);
        encoder.setPattern(PATTERN);
        return encoder;
    }
	
	@Bean (initMethod = "start", destroyMethod = "stop")
    public static ConsoleAppender consoleAppender (LoggerContext loggerContext, PatternLayoutEncoder encoder) {
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(encoder);
        return consoleAppender;
    }
    
	@Bean (initMethod = "start", destroyMethod = "stop")
    public static FileAppender fileAppender(LoggerContext loggerContext, PatternLayoutEncoder encoder) throws IOException {
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(loggerContext);
        fileAppender.setEncoder(encoder);
        fileAppender.setFile("build.log");
        return fileAppender;
    }

	@Bean
    public Logger registerApplicationLogger(LoggerContext loggerContext, ConsoleAppender consoleAppender, FileAppender fileAppender) throws IOException {
    	Logger logger = loggerContext.getLogger("com.thalasoft.user.rest");
        logger.setLevel(Level.DEBUG);
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);
        return logger;
    }

    @Bean
    public Logger registerSpringLogger(LoggerContext loggerContext, ConsoleAppender consoleAppender, FileAppender fileAppender) throws IOException {
    	Logger logger = loggerContext.getLogger("org.springframework");
        logger.setLevel(Level.DEBUG);
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);
        return logger;
    }

}

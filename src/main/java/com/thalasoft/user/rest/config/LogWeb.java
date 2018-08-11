package com.thalasoft.user.rest.config;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;

@Component
public class LogWeb {

	private final static String PATTERN = "%date %-5level [%thread] %logger{36} %m%n %rEx";

	@Bean
    public LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

	@Bean
    public PatternLayoutEncoder patternLayoutEncoder(LoggerContext ctx) {
        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(ctx);
        patternLayoutEncoder.setPattern(PATTERN);
        return patternLayoutEncoder;
    }
	
	@Bean (initMethod = "start", destroyMethod = "stop")
    public ConsoleAppender<ILoggingEvent> consoleAppender(LoggerContext loggerContext, PatternLayoutEncoder patternLayoutEncoder) {
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(patternLayoutEncoder);
        return consoleAppender;
    }
    
	@Bean (initMethod = "start", destroyMethod = "stop")
    public FileAppender<ILoggingEvent> fileAppender(LoggerContext loggerContext, PatternLayoutEncoder patternLayoutEncoder) throws IOException {
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setEncoder(patternLayoutEncoder);
        fileAppender.setFile("build.log");
        return fileAppender;
    }

	@Bean
    public Logger registerApplicationLogger(LoggerContext loggerContext, ConsoleAppender<ILoggingEvent> consoleAppender, FileAppender<ILoggingEvent> fileAppender) throws IOException {
    	Logger logger = loggerContext.getLogger("com.thalasoft.user.rest");
        logger.setLevel(Level.DEBUG);
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);
        return logger;
    }

    @Bean
    public Logger registerSpringLogger(LoggerContext loggerContext, ConsoleAppender<ILoggingEvent> consoleAppender, FileAppender<ILoggingEvent> fileAppender) throws IOException {
    	Logger logger = loggerContext.getLogger("org.springframework");
        logger.setLevel(Level.DEBUG);
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);
        return logger;
    }

}

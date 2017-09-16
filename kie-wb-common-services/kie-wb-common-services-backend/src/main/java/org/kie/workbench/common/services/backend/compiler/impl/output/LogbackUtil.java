package org.kie.workbench.common.services.backend.compiler.impl.output;






/**
 * Created by garfield on 15/09/17.
 */
public class LogbackUtil {

    /*public static org.slf4j.Logger getLogger(String filePath, String name){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        FileAppender fileAppender = new FileAppender();
        fileAppender.setContext(loggerContext);
        fileAppender.setName(name);

        fileAppender.setFile(filePath);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%r %thread %level - %msg%n");
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();

        Logger logbackLogger = loggerContext.getLogger("Main");
        logbackLogger.addAppender(fileAppender);

        //StatusPrinter.print(loggerContext);
        return logbackLogger;
    }*/
/*
    public static Logger boh(String filePath, String name){


        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();


        FileAppender<ILoggingEvent> file = new FileAppender<ILoggingEvent>();
        file.setName(name);
        file.setFile(filePath);
        file.setContext(context);
        file.setAppend(true);


        ThresholdFilter warningFilter = new ThresholdFilter();
        warningFilter.setLevel("INFO");
        warningFilter.setContext(context);
        warningFilter.start();
        file.addFilter(warningFilter);

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setContext(context);
        ple.setPattern("%date %level [%thread] %logger{10} %msg%n");
        ple.start();
        file.setEncoder(ple);

        file.start();


        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        root.addAppender(file);
        return root;
    }*/

}

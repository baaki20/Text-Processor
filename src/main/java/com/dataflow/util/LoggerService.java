package com.dataflow.util;

import java.util.logging.*;

public class LoggerService {

    private static final Logger LOGGER = Logger.getLogger(LoggerService.class.getName());

    static {
        LogManager.getLogManager().reset();
        LOGGER.setLevel(Level.ALL);

        // Console handler
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        LOGGER.addHandler(ch);

        // File handler
        try {
            FileHandler fh = new FileHandler("application.log", true);
            fh.setLevel(Level.FINE);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set up file logger", e);
        }
    }

    public static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void error(String message, Throwable t) {
        LOGGER.log(Level.SEVERE, message, t);
    }

    public static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }
}

package com.mastercard.labs.bps.discovery.filter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name = "LogInterceptor", category = "Core", elementType = "rewritePolicy", printObject = true)
public class CustomLogInterceptor implements RewritePolicy {
    protected static final Logger logger = StatusLogger.getLogger();

    private CustomLogInterceptor() {
    }

    @PluginFactory
    public static CustomLogInterceptor createPolicy() {
        return new CustomLogInterceptor();
    }

    @Override
    public LogEvent rewrite(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();

        // write your code to manipulate your message here
        if(message.toLowerCase().contains("password")) {
            message = "";
        }
        return Log4jLogEvent.newBuilder()
                .setLoggerName(event.getLoggerName())
                .setMarker(event.getMarker())
                .setLoggerFqcn(event.getLoggerFqcn())
                .setLevel(event.getLevel())
                .setMessage(new SimpleMessage(message))
                .setThrown(event.getThrown())
                .setContextMap(event.getContextMap())
                .setContextStack(event.getContextStack())
                .setThreadName(event.getThreadName())
                .setSource(event.getSource())
                .setTimeMillis(event.getTimeMillis())
                .build();
    }
}

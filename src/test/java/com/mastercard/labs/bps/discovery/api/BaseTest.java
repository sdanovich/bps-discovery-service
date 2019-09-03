package com.mastercard.labs.bps.discovery.api;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class BaseTest {

    protected static Properties messages;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    static {
        messages = new Properties();
        try {
            messages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/messages_" + Locale.getDefault().getLanguage() + ".properties"));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        /* setting up positive info */
    }
}

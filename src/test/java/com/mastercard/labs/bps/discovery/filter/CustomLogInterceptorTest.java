package com.mastercard.labs.bps.discovery.filter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomLogInterceptorTest {

    @Mock
    private Message message;

    @Mock
    private LogEvent event;

    @Test
    public void positiveTest() {

        String messageLine = "Exception message";

        CustomLogInterceptor customLogInterceptor = CustomLogInterceptor.createPolicy();

        Mockito.when(message.getFormattedMessage()).thenReturn(messageLine);
        Mockito.when(event.getMessage()).thenReturn(message);

        Assert.assertThat(customLogInterceptor.rewrite(event).getMessage().getFormattedMessage(), Is.is(messageLine));
    }

    @Test
    public void negativeTest() {

        String messageLine = "message contains password";

        CustomLogInterceptor customLogInterceptor = CustomLogInterceptor.createPolicy();

        Mockito.when(message.getFormattedMessage()).thenReturn(messageLine);
        Mockito.when(event.getMessage()).thenReturn(message);

        Assert.assertThat(customLogInterceptor.rewrite(event).getMessage().getFormattedMessage(), Is.is(""));
    }
}
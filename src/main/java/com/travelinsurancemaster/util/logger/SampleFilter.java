package com.travelinsurancemaster.util.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class SampleFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getFormattedMessage().contains("404")) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.ACCEPT;
        }
    }
}
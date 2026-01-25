package com.ichwan.shopper.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;


@Component
public class SessionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);

    @EventListener
    public void handleSessionCreated(SessionCreatedEvent event) {
        logger.info("session created: {}", event.getSessionId());
    }

    @EventListener
    public void handleSessionDeleted(SessionDeletedEvent event) {
        logger.info("session deleted: {}",event.getSessionId());
    }

    @EventListener
    public void handleSessionExpired(SessionExpiredEvent event) {
        logger.info("session expired: {}", event.getSessionId());
    }
}

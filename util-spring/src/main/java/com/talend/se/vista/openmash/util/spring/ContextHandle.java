package com.talend.se.vista.openmash.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

public class ContextHandle implements ApplicationContextAware, InitializingBean {

    static private Logger log = LoggerFactory.getLogger(ContextHandle.class);
    private ApplicationContext applicationContext;
    private String displayName;
    private boolean printConfigLocations = false;

    public ContextHandle() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (applicationContext != null && applicationContext instanceof AbstractApplicationContext) {
            AbstractApplicationContext appContext = (AbstractApplicationContext) applicationContext;
            appContext.setDisplayName(displayName);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (displayName != null) {
            AbstractApplicationContext appContext = (AbstractApplicationContext) applicationContext;
            appContext.setDisplayName(displayName);
        }
        log.info("ContextHandle initialized: " + this.applicationContext.getDisplayName() + " (" + this.applicationContext.getId() + ")");
    }
}

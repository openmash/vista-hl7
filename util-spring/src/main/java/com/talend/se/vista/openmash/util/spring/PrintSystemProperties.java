package com.talend.se.vista.openmash.util.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class PrintSystemProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(PrintSystemProperties.class);

    public PrintSystemProperties() {
    }

    public void printSystemProperties() {
        Properties p = System.getProperties();
        log.info("System Properties");
        ArrayList<Object> propertyKeys = new ArrayList<>();
        propertyKeys.addAll(p.keySet());
        Collections.sort(propertyKeys,
                new Comparator<Object>() {
            @Override
            public int compare(Object s1, Object s2) {
                return s1.toString().compareToIgnoreCase(s2.toString());
            }
        });
        for (Object k : propertyKeys) {
            String key = (String) k;
            String value = (String) p.get(key);
            log.info(key + ": " + value);
        }
        log.info("End System Properties");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        printSystemProperties();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talend.se.vista.openmash.util.spring;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author EdwardOst
 */
public class PrintProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(PrintProperties.class);
    private Map<String, String> propertyList = new TreeMap<>();

    public void setPropertyList(Map<String, String> propertyList) {
        this.propertyList = propertyList;
    }

    public void printProperties() {
        log.info("Property List");
        for (String key : propertyList.keySet()) {
            log.info(key + ": " + propertyList.get(key));
        }
        log.info("End Property List");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        printProperties();
    }
}

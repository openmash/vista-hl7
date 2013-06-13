package com.talend.se.demo.util.spring;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:com/talend/se/demo/util/spring/PrintSystemPropertiesTest-context.xml"
})
public class PrintSystemPropertiesTest {
    
    private static final Logger log = LoggerFactory.getLogger(PrintSystemPropertiesTest.class);
    
    @Autowired
    private ApplicationContext springContext;
    
    
    public PrintSystemPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of printSystemProperties method, of class PropertyList.
     */
    @Test
    public void testLoadingContainer() {
        System.out.println("loadingContainer");

        String container = System.getProperty("container", "spring");
        
        final String expectedContainerPropertyValue = "containerPropertyValue" + "." + container + ".test";
        String testContainerProperty = springContext.getBean("testContainerProperty", String.class);
        assertEquals("testContainerProperty failed: ", expectedContainerPropertyValue, testContainerProperty);

        String testAppProperty = springContext.getBean("testAppProperty", String.class);
        final String expectedAppPropertyValue = "appPropertyValue" + "." + container;
        assertEquals("testAppProperty failed: ", expectedAppPropertyValue, testAppProperty);
    }

}
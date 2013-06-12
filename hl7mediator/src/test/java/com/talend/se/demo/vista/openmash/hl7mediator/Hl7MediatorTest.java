package com.talend.se.demo.vista.openmash.hl7mediator;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ACK;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
/**
 *
 * @author EdwardOst
 *
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:container.xml",
    "classpath:beans.xml",
    "classpath:integration.xml"
})
@MockEndpoints
public class Hl7MediatorTest {

    private static final Logger log = LoggerFactory.getLogger(Hl7MediatorTest.class);
    
    @Autowired
    private ApplicationContext springContext;
    
    @Autowired @Qualifier("hl7mediator")
    private CamelContext camelContext;
    
    @EndpointInject(uri  = "mirth.receiver.jms", context = "hl7mediator")
    private Endpoint mirthReceiverJms;

    @EndpointInject(uri = "mock:log:mirthResponseJms", context="hl7mediator")
    private MockEndpoint testMirthResponse;
    
    @Test
    public void testConsumeFromMirthJms() throws InterruptedException, Exception {
        log.info("################ testConsumeFromMirthJms ################");
        testMirthResponse.expectedMessageCount(1);
        String fileUri = "file:src/test/resources/com/talend/se/demo/vista/openmash/hl7mediator/consumeFromMirth?noop=true&fileName=ORU_R01_tim_mirthJms.er7";
        String resultUri = "mock:ignoreJmsResult";
        RouteBuilder routeBuilder = createTestFixture("testConsumeFromMirthJms", fileUri, mirthReceiverJms, resultUri);
        camelContext.addRoutes(routeBuilder);
        routeBuilder = createUnmarshallerRoute();
        camelContext.addRoutes(routeBuilder);
        testMirthResponse.assertIsSatisfied();
        ACK ack = (ACK) unmarshallHL7(testMirthResponse.getReceivedExchanges().get(0).getIn().getBody(String.class));
        testAckMessage(ack);
    }

//    @EndpointInject(uri = "mirth.listener.mllp", context = "hl7mediator")
    @EndpointInject(uri = "mirthListenerEndpoint", context = "hl7mediator")
    private Endpoint mirthListenerMllp;

    @Test
    public void testConsumeFromMirthMllp() throws InterruptedException, Exception {
        log.info("################ testConsumeFromMirthMllp ################");
        String fileUri = "file:src/test/resources/com/talend/se/demo/vista/openmash/hl7mediator/consumeFromMirth?noop=true&fileName=ORU_R01_tim_mirthMllp.er7";
        String resultUri = "mock:mirthMllpResult";
        RouteBuilder routeBuilder = createTestFixture("testConsumeFromMirthMllp", fileUri, mirthListenerMllp, resultUri);
        camelContext.addRoutes(routeBuilder);
        MockEndpoint mockResult = MockEndpoint.resolve(camelContext, resultUri);
        mockResult.expectedMessageCount(1);
        mockResult.assertIsSatisfied();
        testAckMessage(mockResult.getReceivedExchanges().get(0).getIn().getBody(ACK.class));
    }


//    @EndpointInject(uri  = "vista.listener.mllp", context = "hl7mediator")
    @EndpointInject(uri  = "vistaListenerEndpoint", context = "hl7mediator")
    private Endpoint vistaListenerMllp;
    
    @Test
    public void testConsumeFromVistaMllp() throws InterruptedException, Exception {
        log.info("################ testConsumeFromVistaMllp ################");
        String fileUri = "file:src/test/resources/com/talend/se/demo/vista/openmash/hl7mediator/consumeFromMirth?noop=true&fileName=ORU_R01_tim_vistaMllp.er7";
        String resultUri = "mock:vistaMllpResult";
        RouteBuilder routeBuilder = createTestFixture("testConsumeFromVistaMllp", fileUri, vistaListenerMllp, resultUri);
        camelContext.addRoutes(routeBuilder);
        MockEndpoint mockResult = camelContext.getEndpoint(resultUri, MockEndpoint.class);
        mockResult.expectedMessageCount(1);
        mockResult.assertIsSatisfied();
        testAckMessage(mockResult.getReceivedExchanges().get(0).getIn().getBody(ACK.class));

    }

    private RouteBuilder createTestFixture(final String testName, final String fileUri, final Endpoint unitUnderTest, final String resultUri) {
        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(fileUri).convertBodyTo(String.class)
                        .log("################ start " + testName + " ##############")
                        .to(unitUnderTest)
                        .log("################# end " + testName + " #############")
                        .unmarshal().hl7()
                        .to(resultUri);
            }
            
        };
        return routeBuilder;
    }
    
    private Message unmarshallHL7(String msg) throws Exception {
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Message hl7Message = producerTemplate.requestBody("direct:hl7Unmarshaller", msg, Message.class);
        return hl7Message;
    }
    
    private RouteBuilder createUnmarshallerRoute() {
        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:hl7Unmarshaller").unmarshal().hl7();
            }
        };
        return routeBuilder;
    }
    
    private void testAckMessage(ACK ack) throws InterruptedException {
        // expected result
        // MSH|^~\&|XXXX|TE123456|1100|PO|20130610091526||ACK|20130610091526|P|2.3
        // MSA|AA|0416
        //messages Received = 1
        //sendingApplication: HD[XXXX]
        //sendingFacility: HD[TE123456]
        //receivingApplication: HD[1100]
        //receivingFacility: HD[PO]
        //messageType: CM_MSG[ACK]
        //datetime: TS[20130610094831]
        //processingId: PT[P]
        //versionID: 2.3
        // acknowledgementCode: AA
        // messageControlId: 0416
        assertEquals("sendingApplication failed: ", "HD[XXXX]", ack.getMSH().getSendingApplication().toString());
        assertEquals("sendingFacility failed: ", "HD[TE123456]", ack.getMSH().getSendingFacility().toString());
        assertEquals("receivingApplication failed: ", "HD[1100]", ack.getMSH().getReceivingApplication().toString());
        assertEquals("receivingFacility failed: ", "HD[PO]", ack.getMSH().getReceivingFacility().toString());
        assertEquals("messageType failed: ", "CM_MSG[ACK]", ack.getMSH().getMessageType().toString());
        assertEquals("processingId failed: ", "PT[P]", ack.getMSH().getProcessingID().toString());
        assertEquals("versionID failed: ", "2.3", ack.getMSH().getVersionID().toString());
        assertEquals("acknowledgementCode failed: ", "AA", ack.getMSA().getAcknowledgementCode().toString());
        assertEquals("messageControlId failed: ", "0416",  ack.getMSA().getMessageControlID().toString());
    }

}

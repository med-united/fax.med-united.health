package health.medunited.artemis;

import health.medunited.model.PrescriptionRequest;
import health.medunited.service.XSLTService;
import org.json.JSONObject;
import org.json.XML;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.jms.*;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class PrescriptionConsumer implements Callable<Void> {

    private static final Logger log = Logger.getLogger(PrescriptionConsumer.class.getName());

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    XSLTService xsltService;

    private File xslFile;

    private static final String PVS_HEADER = "practiceManagementTranslation";
    private static final String FINGERPRINT_HEADER = "receiverPublicKeyFingerprint";

    public void setTemplateFileForPDFGeneration(File xslFile) {
        this.xslFile = xslFile;
    }

    @Override
    public Void call() {

        log.info("inside consumer");
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            Queue queue = context.createQueue("Prescriptions");
            log.info("inside try block");
            try (JMSConsumer consumer = context.createConsumer(queue, "practiceManagementTranslation = 'fax'")) {
                while (true) {
                    try {
                        Message message = consumer.receive();
                        if (message == null) {
                            continue;
                        }
                        if (message.propertyExists(FINGERPRINT_HEADER) && message.propertyExists(PVS_HEADER)) {
                            String practiceManagement = message.getObjectProperty(PVS_HEADER).toString();
                            String fhirBundle = getFhirBundleFromBytesMessage((BytesMessage) message);
                            log.info(practiceManagement);
                            JSONObject json = new JSONObject(fhirBundle);
                            String xmlBundle = XML.toString(json);
                            PrescriptionRequest prescription = new PrescriptionRequest(practiceManagement, fhirBundle);

                            String xmlStringWithBundle = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                                    "<root>" + xmlBundle + "</root>";

                            xsltService.generatePDF(xslFile, xmlStringWithBundle);

                            log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                        } else {
                            log.info("Invalid content");
                        }
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Problem with processing message", e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getFhirBundleFromBytesMessage(BytesMessage message) throws JMSException {
        byte[] byteData = new byte[(int) message.getBodyLength()];
        message.readBytes(byteData);
        message.reset();
        return new String(byteData);
    }
}

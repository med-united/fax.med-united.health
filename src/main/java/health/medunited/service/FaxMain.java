package health.medunited.service;

import health.medunited.artemis.PrescriptionConsumer;
import health.medunited.model.PrescriptionRequest;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.fop.apps.FOPException;
import org.json.JSONObject;
import org.json.XML;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

@QuarkusMain
public class FaxMain implements QuarkusApplication {

    private static final Logger log = Logger.getLogger(FaxMain.class.getName());

    @Inject
    XSLTService xsltService;

    @Inject
    PrescriptionConsumer prescriptionConsumerProvider;

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = FaxMain.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());
            return new File(resource.toURI());
        }
    }

    private void printFile(File file) {

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int run(String[] args) throws FOPException, IOException, URISyntaxException {

        log.info("Running main method ------------");

        Void consumer = prescriptionConsumerProvider.call();
        log.info("PrescriptionConsumer");

        String xmlFileName = "xslt/example.xml";
        log.info("getResource : " + xmlFileName);
        File xmlFile = FaxMain.getFileFromResource(xmlFileName);
        //printFile(xmlFile);

        String xslFileName = "xslt/example.xsl";
        log.info("getResource : " + xslFileName);
        File xslFile = FaxMain.getFileFromResource(xslFileName);
        //printFile(xslFile);

        String jsonFileName = "xslt/exampleBundle.json";
        log.info("getResource : " + jsonFileName);
        File jsonFile = FaxMain.getFileFromResource(jsonFileName);
        //printFile(jsonFile);
        String jsonBundle = Files.readString(Path.of(jsonFile.getPath()));

        JSONObject json = new JSONObject(jsonBundle);
        String xml = XML.toString(json);
        //log.info(xml);

        xsltService.generatePDF(xmlFile, xslFile);
        return 0;
    }
}

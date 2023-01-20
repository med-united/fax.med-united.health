package health.medunited.service;

import org.apache.fop.apps.FOPException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class FaxService {
    private static final Logger log = Logger.getLogger(FaxService.class.getName());

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = FaxService.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());
            return new File(resource.toURI());
        }
    }

    private static void printFile(File file) {

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws URISyntaxException, FOPException, IOException {

        String xmlFileName = "xslt/example.xml";
        log.info("getResource : " + xmlFileName);
        File xmlFile = FaxService.getFileFromResource(xmlFileName);
        //printFile(xmlFile);

        String xslFileName = "xslt/example.xsl";
        log.info("getResource : " + xslFileName);
        File xslFile = FaxService.getFileFromResource(xslFileName);
        //printFile(xslFile);

        String jsonFileName = "xslt/exampleBundle.json";
        log.info("getResource : " + jsonFileName);
        File jsonFile = FaxService.getFileFromResource(jsonFileName);
        //printFile(jsonFile);
        String jsonBundle = Files.readString(Path.of(jsonFile.getPath()));

        JSONObject json = new JSONObject(jsonBundle);
        String xml = XML.toString(json);
        //log.info(xml);

        XSLTService.generatePDF(xmlFile, xslFile);
    }
}

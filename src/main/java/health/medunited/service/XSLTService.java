package health.medunited.service;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

import org.apache.fop.apps.*;

public class XSLTService {

    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
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

    public static void main(String[] args) throws URISyntaxException, FOPException, IOException, TransformerException {

        Logger log = Logger.getLogger(XSLTService.class.getName());
        XSLTService app = new XSLTService();

        String xmlFileName = "xslt/example.xml";

        log.info("\ngetResource : " + xmlFileName);
        File xmlFile = app.getFileFromResource(xmlFileName);
        //printFile(xmlFile);

        String xslFileName = "xslt/example.xsl";

        log.info("\ngetResource : " + xslFileName);
        File xslFile = app.getFileFromResource(xslFileName);
        //printFile(xslFile);

        File pdfFile = new File("src/main/resources/xslt", "generated.pdf");
        System.out.println(pdfFile.getAbsolutePath());

        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        OutputStream out = new FileOutputStream(pdfFile);
        out = new BufferedOutputStream(out);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

            Source src = new StreamSource(xmlFile);

            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(src, res);
        } finally {
            out.close();
        }
    }
}

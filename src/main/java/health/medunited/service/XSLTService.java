package health.medunited.service;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.logging.Logger;

import org.apache.fop.apps.*;

public class XSLTService {

    private static final Logger log = Logger.getLogger(XSLTService.class.getName());

    static void generatePDF(File xmlFile, File xslFile) throws IOException, FOPException {

        File pdfFile = new File("src/main/resources/xslt", "generated.pdf");
        log.info(pdfFile.getAbsolutePath());

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

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}

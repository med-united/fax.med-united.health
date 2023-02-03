package health.medunited;

import health.medunited.artemis.PrescriptionConsumer;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@QuarkusMain
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Quarkus.run(Fax.class, args);
    }

    public static class Fax implements QuarkusApplication {

        @Inject
        PrescriptionConsumer prescriptionConsumerProvider;

        @Override
        public int run(String... args) {

            log.info("Inside run() method ------------");

            String xslFileName = "template.xsl";
            log.info("getResource : " + xslFileName);
            ClassLoader classLoader = getClass().getClassLoader();
            File xslFile = new File(Objects.requireNonNull(classLoader.getResource(xslFileName)).getFile());
            printFile(xslFile);

            prescriptionConsumerProvider.setTemplateFileForPDFGeneration(xslFile);
            prescriptionConsumerProvider.call();

            Quarkus.waitForExit();
            return 0;
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
}

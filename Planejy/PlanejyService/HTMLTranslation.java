import com.azure.ai.texttranslation.TextTranslationClient;
import com.azure.ai.texttranslation.TextTranslationClientBuilder;
import com.azure.ai.texttranslation.models.TranslateOptions;
import com.azure.ai.texttranslation.models.TranslationResult;
import com.azure.core.credential.AzureKeyCredential;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class HTMLTranslationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HTMLTranslationApplication.class, args);
    }

    @RestController
    public static class TranslationController {

        @PostMapping("/translate")
        public List<String> translateHTML(@RequestBody TranslationRequest translationRequest) {
            List<String> htmlPaths = Arrays.asList(
                "target/classes/Configuracoes/index.html",
                "target/classes/Login/about.html",
                "target/classes/Login/register.html",
                "target/classes/Login/reset-passwd.html",
                "target/classes/Login/passwd-recovery.html",
                "target/classes/Calendario/Calendario.html",
                "target/classes/Artigos/Artigos.html",
                "target/classes/index.html"
            );

            List<String> translatedHtmlPaths = new ArrayList<>();

            for (String htmlPath : htmlPaths) {
                String html = readHtmlFromFile(htmlPath);
                if (html != null) {
                    String translatedHTML = translateHTML(html, translationRequest.getSelectedValue());
                    String translatedFilePath = getTranslatedFilePath(htmlPath, translationRequest.getSelectedValue());
                    saveTranslatedHTMLToFile(translatedHTML, translatedFilePath);
                    translatedHtmlPaths.add(translatedFilePath);
                }
            }

            return translatedHtmlPaths;
        }

        private String translateHTML(String html, String targetLanguage) {
            String subscriptionKey = "c5262cf2e62841e6a745720aad962333";
            String endpoint = "https://api.cognitive.microsofttranslator.com/";

            // Criar cliente de tradução
            TextTranslationClient translationClient = new TextTranslationClientBuilder()
                    .credential(new AzureKeyCredential(subscriptionKey))
                    .endpoint(endpoint)
                    .buildClient();

            // Definir opções de tradução
            TranslateOptions translateOptions = new TranslateOptions(html)
                    .setTargetLanguage(targetLanguage);

            // Realizar a tradução
            TranslationResult result = translationClient.translate(translateOptions);

            // Retornar o HTML traduzido
            return result.getTranslatedText();
        }

        private String getTranslatedFilePath(String htmlFilePath, String targetLanguage) {
            File htmlFile = new File(htmlFilePath);
            File parentDirectory = htmlFile.getParentFile();
            String fileName = htmlFile.getName().replace(".html", "");
            return parentDirectory.getAbsolutePath() + "/" + fileName + "_" + targetLanguage + ".html";
        }

        private void saveTranslatedHTMLToFile(String translatedHTML, String filePath) {
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(translatedHTML);
                System.out.println("HTML traduzido salvo em arquivo com sucesso: " + filePath);
            } catch (IOException e) {
                System.out.println("Erro ao salvar o HTML traduzido em arquivo: " + e.getMessage());
            }
        }

        private String readHtmlFromFile(String filePath) {
            try {
                byte[] encodedBytes = Files.readAllBytes(Paths.get(filePath));
                return new String(encodedBytes);
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo HTML: " + e.getMessage());
                return null;
            }
        }

        static class TranslationRequest {
            private String selectedValue;

            public String getSelectedValue() {
                return selectedValue;
            }

            public void setSelectedValue(String selectedValue) {
                this.selectedValue = selectedValue;
            }
        }
    }
}

import com.azure.ai.texttranslation.TextTranslationClient;
import com.azure.ai.texttranslation.TextTranslationClientBuilder;
import com.azure.ai.texttranslation.models.TranslateOptions;
import com.azure.ai.texttranslation.models.TranslationResult;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SpringBootApplication
public class TranslationApplication {
    private final TextTranslationClient translationClient;

    public TranslationApplication() {
        String subscriptionKey = "c5262cf2e62841e6a745720aad962333";
        String endpoint = "https://api.cognitive.microsofttranslator.com/";

        // Criar cliente de tradução
        this.translationClient = new TextTranslationClientBuilder()
                .credential(new AzureKeyCredential(subscriptionKey))
                .endpoint(endpoint)
                .buildClient();
    }

    public static void main(String[] args) {
        SpringApplication.run(TranslationApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/translate")
    public String translate(@RequestParam("inputText") String inputText,
                            Model model) {
        // Definir idioma padrão
        String defaultLanguage = "pt";

        // Traduzir o texto para o idioma padrão
        String translatedText = translateText(inputText, defaultLanguage);

        // Exibir o texto traduzido
        model.addAttribute("translatedText", translatedText);

        // Salvar o texto traduzido no banco de dados
        saveTranslatedTextToDatabase(translatedText);

        return "result";
    }

    private String translateText(String text, String targetLanguage) {
        // Definir opções de tradução
        TranslateOptions translateOptions = new TranslateOptions(text)
                .setTargetLanguage(targetLanguage);

        // Realizar a tradução
        TranslationResult result = translationClient.translate(translateOptions);

        // Retornar o texto traduzido
        return result.getTranslatedText();
    }

    private void saveTranslatedTextToDatabase(String translatedText) {
        // Lógica para salvar o texto traduzido no banco de dados
        String url = "jdbc:postgresql://localhost:5432/planejy";
        String username = "ti2cc";
        String password = "ti@cc";
        String sql = "INSERT INTO translated_text (text) VALUES (?)";
    
        try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, translatedText);
            pstmt.executeUpdate();
            System.out.println("Texto traduzido salvo no banco de dados com sucesso.");
            } catch (SQLException e) {
               System.out.println("Erro ao salvar o texto traduzido no banco de dados: " + e.getMessage());
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.FileOutputStream;

public class AIBiblicalConcordanceGPT extends JFrame {

    private JTextField promptField;
    private JButton submitButton;
    private Map<String, String> assistants;
    private Map<String, JTextArea> responseAreas;

    String instructions = Instructions.AIBiblicalConcordanceGPT;
    String promptValidationTask = PromptValidationTask.promptValidationTask;
    String searchCriteriaExpansionTask = SearchCriteriaExpansionTask.searchCriteriaExpansionTask;

    private final String plainTextStyle = """
            When responding to user queries, please use plain or rich text format. Do not use markdown, HTML, or any other markup languages.
            """;

    public AIBiblicalConcordanceGPT() {
        setTitle("GPT Assistants App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initAssistants();
        initComponents();
    }

    private void initAssistants() {
        assistants = new LinkedHashMap<>();
        responseAreas = new LinkedHashMap<>();

        // Initialize assistants with their system prompts
        assistants.put("AIBiblicalConcordanceGPT", instructions + promptValidationTask + searchCriteriaExpansionTask);
    }

    private void initComponents() {
        // Prompt input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        promptField = new JTextField();
        submitButton = new JButton("Submit");

        inputPanel.add(new JLabel("Enter your prompt:"), BorderLayout.NORTH);
        inputPanel.add(promptField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        // Tabbed pane to hold response areas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Initialize response areas for each assistant
        for (String assistantName : assistants.keySet()) {
            JTextArea responseArea = new JTextArea();
            responseArea.setEditable(false);
            responseArea.setLineWrap(true);
            responseArea.setWrapStyleWord(true);
            responseAreas.put(assistantName, responseArea);

            JPanel responsePanel = new JPanel(new BorderLayout());
            responsePanel.add(new JScrollPane(responseArea), BorderLayout.CENTER);

            tabbedPane.addTab(assistantName, responsePanel);
        }

        // Main frame layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Action listener for the submit button
        submitButton.addActionListener(e -> {
            String prompt = promptField.getText().trim();
            if (!prompt.isEmpty()) {
                submitButton.setEnabled(false);

                // Clear previous responses
                for (JTextArea responseArea : responseAreas.values()) {
                    responseArea.setText("");
                }

                // Start a new thread for each assistant
                for (String assistantName : assistants.keySet()) {
                    String systemPrompt = assistants.get(assistantName);
                    JTextArea responseArea = responseAreas.get(assistantName);
                    new Thread(() -> sendRequest(assistantName, prompt, plainTextStyle + systemPrompt, responseArea)).start();
                }
            }
        });

        // Add a "Save as RTF" button in initComponents
        JButton saveRtfButton = new JButton("Save Responses as RTF");
        saveRtfButton.addActionListener(e -> {
            for (String assistantName : responseAreas.keySet()) {
                saveResponseAsRTF(assistantName, responseAreas.get(assistantName));
            }
        });

        inputPanel.add(saveRtfButton, BorderLayout.WEST);
    }

    private void sendRequest(String assistantName, String userPrompt, String systemPrompt, JTextArea responseArea) {
        // Get your OpenAI API key from an environment variable
        String apiKey = "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";

        // OpenAI API endpoint
        String apiURL = "https://api.openai.com/v1/chat/completions";

        // Build the request body
        String requestBody = "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"" + escapeJson(systemPrompt) + "\"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + escapeJson(userPrompt) + "\"}\n" +
                "  ],\n" +
                "  \"max_tokens\": 16384,\n" +
                "  \"stream\": true,\n" +
                "  \"response_format\": {\n" +
                "    \"type\": \"json_schema\",\n" +
                "    \"json_schema\": {\n" +
                "      \"name\": \"ConcordanceAnalysisSchema\",\n" +
                "      \"description\": \"Structured output for AIBiblicalConcordanceGPT analysis of Bible verses based on specified criteria\",\n" +
                "      \"strict\": true,\n" +
                "      \"schema\": {\n" +
                "        \"type\": \"object\",\n" +
                "        \"properties\": {\n" +
                "          \"prompt_analysis\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"description\": \"Evaluation and feedback on prompt components\",\n" +
                "            \"properties\": {\n" +
                "              \"verse_reference_check\": {\n" +
                "                \"type\": \"string\",\n" +
                "                \"description\": \"Feedback on whether the verse reference specified in the prompt is valid\"\n" +
                "              },\n" +
                "              \"verse_validity\": {\n" +
                "                \"type\": \"string\",\n" +
                "                \"description\": \"Feedback on the comprehensiveness and specificity of the verse selection\"\n" +
                "              },\n" +
                "              \"search_criteria_evaluation\": {\n" +
                "                \"type\": \"string\",\n" +
                "                \"description\": \"Feedback on the conformity and clarity of the search criteria specified in the prompt\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"required\": [\"verse_reference_check\", \"verse_validity\", \"search_criteria_evaluation\"],\n" +
                "            \"additionalProperties\": false\n" +
                "          },\n" +
                "          \"search_criteria_expansion\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"description\": \"Expanded terms generated by each search expansion methodology\",\n" +
                "            \"properties\": {\n" +
                "              \"qualifiers\": {\"type\": \"array\", \"description\": \"List of qualifiers\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"lexical_semantic_relationships\": {\"type\": \"array\", \"description\": \"List of lexical semantic relationships\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"semantic_networks\": {\"type\": \"array\", \"description\": \"List of semantic networks\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"conceptual_similarity\": {\"type\": \"array\", \"description\": \"List of conceptually similar terms\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"ontologies\": {\"type\": \"array\", \"description\": \"List of related terms from ontologies\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"latent_semantic_analysis\": {\"type\": \"array\", \"description\": \"List of terms from Latent Semantic Analysis\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"latent_dirichlet_allocation\": {\"type\": \"array\", \"description\": \"List of terms from Latent Dirichlet Allocation\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"lexical_expansion\": {\"type\": \"array\", \"description\": \"List of lexical expansions\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"taxonomies\": {\"type\": \"array\", \"description\": \"List of taxonomic classifications\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"topic_modeling\": {\"type\": \"array\", \"description\": \"List of related topics\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"fuzzy_matching\": {\"type\": \"array\", \"description\": \"List of fuzzy matches\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"fuzzy_logic\": {\"type\": \"array\", \"description\": \"List of fuzzy logic matches\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"subconcept\": {\"type\": \"array\", \"description\": \"List of subconcepts\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"superconcept\": {\"type\": \"array\", \"description\": \"List of superconcepts\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"subset\": {\"type\": \"array\", \"description\": \"List of subsets\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"superset\": {\"type\": \"array\", \"description\": \"List of supersets\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"subdomain\": {\"type\": \"array\", \"description\": \"List of subdomains\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"domain\": {\"type\": \"array\", \"description\": \"List of domains\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"instance\": {\"type\": \"array\", \"description\": \"List of instances\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"class\": {\"type\": \"array\", \"description\": \"List of classes\", \"items\": {\"type\": \"string\"}},\n" +
                "              \"contextual_hierarchies\": {\"type\": \"array\", \"description\": \"List of contextual hierarchies (hypernyms and hyponyms)\", \"items\": {\"type\": \"string\"}}\n" +
                "            },\n" +
                "            \"required\": [\n" +
                "              \"qualifiers\", \"lexical_semantic_relationships\", \"semantic_networks\", \"conceptual_similarity\", \"ontologies\",\n" +
                "              \"latent_semantic_analysis\", \"latent_dirichlet_allocation\", \"lexical_expansion\", \"taxonomies\", \"topic_modeling\",\n" +
                "              \"fuzzy_matching\", \"fuzzy_logic\", \"subconcept\", \"superconcept\", \"subset\", \"superset\", \"subdomain\", \"domain\",\n" +
                "              \"instance\", \"class\", \"contextual_hierarchies\"\n" +
                "            ],\n" +
                "            \"additionalProperties\": false\n" +
                "          },\n" +
                "          \"verse_range\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"description\": \"Range of Bible verses specified by the user\"\n" +
                "          },\n" +
                "          \"search_criteria\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"description\": \"Search criteria applied to the verses\"\n" +
                "          },\n" +
                "          \"results\": {\n" +
                "            \"type\": \"array\",\n" +
                "            \"description\": \"Array of evaluated verses and their match probabilities\",\n" +
                "            \"items\": {\n" +
                "              \"type\": \"object\",\n" +
                "              \"properties\": {\n" +
                "                \"reference\": {\n" +
                "                  \"type\": \"string\",\n" +
                "                  \"description\": \"Book, chapter, and verse(s) of the matched verse (e.g., 'Romans 3:24')\"\n" +
                "                },\n" +
                "                \"text\": {\n" +
                "                  \"type\": \"string\",\n" +
                "                  \"description\": \"Text of the matched Bible verse\"\n" +
                "                },\n" +
                "                \"version\": {\n" +
                "                  \"type\": \"string\",\n" +
                "                  \"description\": \"Bible version used\"\n" +
                "                },\n" +
                "                \"match_probability\": {\n" +
                "                  \"type\": \"string\",\n" +
                "                  \"enum\": [\"High\", \"Medium\", \"Low\", \"None\"],\n" +
                "                  \"description\": \"Probability of the match based on the applied search criteria\"\n" +
                "                }\n" +
                "              },\n" +
                "              \"required\": [\"reference\", \"text\", \"version\", \"match_probability\"],\n" +
                "              \"additionalProperties\": false\n" +
                "            }\n" +
                "          },\n" +
                "          \"summary\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"description\": \"Summary of findings across match probability categories\",\n" +
                "            \"properties\": {\n" +
                "              \"total_verses_matched\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Total number of verses that matched any probability category (High, Medium, or Low)\"\n" +
                "              },\n" +
                "              \"total_verses_tested\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Total number of verses that were tested for a match\"\n" +
                "              },\n" +
                "              \"high_matches\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Count of verses with High match probability\"\n" +
                "              },\n" +
                "              \"medium_matches\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Count of verses with Medium match probability\"\n" +
                "              },\n" +
                "              \"low_matches\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Count of verses with Low match probability\"\n" +
                "              },\n" +
                "              \"none_matches\": {\n" +
                "                \"type\": \"integer\",\n" +
                "                \"description\": \"Count of verses with None as match probability\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"required\": [\"total_verses_matched\", \"total_verses_tested\", \"high_matches\", \"medium_matches\", \"low_matches\", \"none_matches\"],\n" +
                "            \"additionalProperties\": false\n" +
                "          },\n" +
                "          \"next_steps\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"description\": \"Suggested next steps based on user’s choice, such as refining the search criteria or re-running the search with a new range of verses\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"required\": [\"prompt_analysis\", \"search_criteria_expansion\", \"verse_range\", \"search_criteria\", \"results\", \"summary\", \"next_steps\"],\n" +
                "        \"additionalProperties\": false\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String selectedRequestBody = requestBody;

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .timeout(Duration.ofMinutes(5))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(selectedRequestBody))
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                // Read the error response body
                String errorBody = new String(response.body().readAllBytes());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error: " + response.statusCode() + " - " + errorBody,
                            "API Error",
                            JOptionPane.ERROR_MESSAGE);
                    submitButton.setEnabled(true);
                });
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring("data: ".length()).trim();

                    if (data.equals("[DONE]")) {
                        break;
                    }

                    // Now parse the JSON in data
                    String content = extractContent(data);

                    if (content != null) {
                        SwingUtilities.invokeLater(() -> responseArea.append(content));
                    }
                }
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "An error occurred:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                submitButton.setEnabled(true);
            });
            e.printStackTrace();
        } finally {
            // Check if all responses are done
            SwingUtilities.invokeLater(() -> {
                boolean allDone = true;
                for (JTextArea area : responseAreas.values()) {
                    if (area.getText().isEmpty()) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    submitButton.setEnabled(true);
                }
            });
        }
    }

    private static String extractContent(String jsonData) {
        // Naive JSON parsing to extract the "content" field
        int contentIndex = jsonData.indexOf("\"content\":");
        if (contentIndex == -1) {
            return null;
        }

        int startQuoteIndex = jsonData.indexOf("\"", contentIndex + "\"content\":".length());
        if (startQuoteIndex == -1) {
            return null;
        }

        int endQuoteIndex = jsonData.indexOf("\"", startQuoteIndex + 1);
        while (endQuoteIndex != -1 && jsonData.charAt(endQuoteIndex - 1) == '\\') {
            // Skip escaped quote
            endQuoteIndex = jsonData.indexOf("\"", endQuoteIndex + 1);
        }
        if (endQuoteIndex == -1) {
            return null;
        }

        String content = jsonData.substring(startQuoteIndex + 1, endQuoteIndex);

        // Unescape escaped characters
        content = content.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        return content;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public void saveResponseAsRTF(String assistantName, JTextArea responseArea) {
        try {
            DefaultStyledDocument doc = new DefaultStyledDocument();
            doc.insertString(0, responseArea.getText(), null);

            RTFEditorKit rtfEditor = new RTFEditorKit();
            try (FileOutputStream out = new FileOutputStream(assistantName + "_response.rtf")) {
                rtfEditor.write(out, doc, 0, doc.getLength());
            }

            JOptionPane.showMessageDialog(this, "Response saved as " + assistantName + "_response.rtf");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving response as RTF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AIBiblicalConcordanceGPT app = new AIBiblicalConcordanceGPT();
            app.setVisible(true);
        });
    }
}
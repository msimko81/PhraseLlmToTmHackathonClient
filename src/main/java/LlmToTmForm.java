import com.phrase.hackathon.BackendClient;
import com.phrase.hackathon.Dataset;
import com.phrase.hackathon.DatasetIdNameResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LlmToTmForm extends JFrame {
    private BackendClient backendClient = new BackendClient();
    private BackendClient.PromptType promptType;

    private Dataset originalDataset;
    private Dataset dataset;
    private DatasetIdNameResponse selectedDataset;
    private String selectedGptModel;

    public static void main(String[] args) {
        new LlmToTmForm();
    }

    private LlmToTmForm() {
        super("LLM to TMs");
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loadDatasets();
        loadGptModels();

        setVisible(true);
        datasetList.addActionListener(e -> {
            selectedDataset = (DatasetIdNameResponse) datasetList.getSelectedItem();
            showSelectedDatasetSegments(selectedDataset);
        });

        promptButton.addActionListener(e -> {
            promptChatbot();
        });
        genderNeutralButton.addActionListener(e -> {
            promptType = BackendClient.PromptType.Transform;
            promptTextArea.setText("Rewrite the following in a gender neutral way.");
        });
        replaceAllOccurencesButton.addActionListener(e -> {
            promptType = BackendClient.PromptType.Transform;
            promptTextArea.setText("Rewrite the following by replacing all occurrences of 'NHL' for 'ELH'.");
        });
        propagateChangeButton.addActionListener(e -> {
            promptType = BackendClient.PromptType.PropagateChange;
            promptTextArea.setText(dataset.getSegments().stream().findFirst().map(Dataset.Segment::getSource).orElse(""));
        });
        clearButton.addActionListener(e -> renderSegmentsToTable(originalDataset));
        gptModelList.addActionListener(e -> {
            selectedGptModel = (String) gptModelList.getSelectedItem();
        });
    }

    private void promptChatbot() {
        Dataset dataset = backendClient.vaporiseDataset(selectedDataset.getId(), promptTextArea.getText(), promptType, selectedGptModel);
        renderSegmentsToTable(dataset);
        sampleSegmentsPanel.setVisible(true);
    }

    private void showSelectedDatasetSegments(DatasetIdNameResponse selection) {
        dataset = backendClient.getDataset(selection.getId());
        originalDataset = dataset;

        sampleSegmentsLabel.setText("%s sample segments".formatted(dataset.getName()));
        sampleSourceLanguageLabel.setText("Source %s".formatted(dataset.getSourceLanguage()));
        sampleTargetLanguageLabel.setText("Target %s".formatted(dataset.getTargetLanguage()));

        renderSegmentsToTable(dataset);
    }

    private void renderSegmentsToTable(Dataset dataset) {
        DefaultTableModel model = (DefaultTableModel) sampleSegmentsTable.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);

        model.addColumn("Source");
        model.addColumn("Target");

        dataset.getSegments().forEach(segment ->
                model.addRow(new String[]{segment.getSource(), segment.getTarget()})
        );
    }

    private void loadDatasets() {
        backendClient.listDatasets().forEach(datasetList::addItem);
    }

    private void loadGptModels() {
        backendClient.listSupportedGptModels().forEach(gptModelList::addItem);
    }

    private JPanel rootPanel;
    private JComboBox datasetList;
    private JPanel sampleSegmentsPanel;
    private JPanel datasetSelectPanel;
    private JLabel sampleSourceLanguageLabel;
    private JLabel sampleTargetLanguageLabel;
    private JTable sampleSegmentsTable;
    private JLabel sampleSegmentsLabel;
    private JPanel playgroundPanel;
    private JTextArea promptTextArea;
    private JButton promptButton;
    private JButton genderNeutralButton;
    private JButton replaceAllOccurencesButton;
    private JButton propagateChangeButton;
    private JButton clearButton;
    private JComboBox gptModelList;
}

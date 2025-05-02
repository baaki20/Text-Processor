package com.dataflow.controller;

import com.dataflow.service.FileProcessingService;
import com.dataflow.model.TextRecord;
import com.dataflow.service.RegexService;
import com.dataflow.service.DataManagementService;
import com.dataflow.service.StreamProcessingService;
import com.dataflow.service.TextProcessingService;
import com.dataflow.util.LoggerUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    // Services
    private final RegexService regexService = new RegexService();
    private final FileProcessingService fileProcessingService = new FileProcessingService();
    private final StreamProcessingService streamProcessingService = new StreamProcessingService();
    private final TextProcessingService textProcessingService = new TextProcessingService();
    private final DataManagementService dataManagementService = new DataManagementService();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // FXML components for Regex Operations tab
    @FXML private TextField regexPatternField;
    @FXML private ComboBox<String> operationComboBox;
    @FXML private TextField replacementField;
    @FXML private TextArea inputTextArea;
    @FXML private TextArea outputTextArea;
    @FXML private TableView<Map<String, Object>> matchesTableView;
    @FXML private TableColumn<Map<String, Object>, String> matchTextColumn;
    @FXML private TableColumn<Map<String, Object>, Integer> startIndexColumn;
    @FXML private TableColumn<Map<String, Object>, Integer> endIndexColumn;

    // FXML components for Text Analytics tab
    @FXML private Spinner<Integer> minCountSpinner;
    @FXML private TextField filterPatternField;
    @FXML private TextArea analyticsResultsArea;
    @FXML private TableView<Pair<String, String>> analyticsTableView;
    @FXML private TableColumn<Pair<String, String>, String> keyColumn;
    @FXML private TableColumn<Pair<String, String>, String> valueColumn;

    // FXML components for Data Management tab
    @FXML private TableView<TextRecord> recordsTableView;
    @FXML private TableColumn<TextRecord, String> recordIdColumn;
    @FXML private TableColumn<TextRecord, String> recordLabelColumn;
    @FXML private TableColumn<TextRecord, String> recordContentColumn;
    @FXML private TextField recordIdField;
    @FXML private TextField recordLabelField;
    @FXML private TextArea recordContentArea;

    // UI components for status updates
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private TabPane mainTabPane;

    // Collections for UI data binding
    private ObservableList<Map<String, Object>> matchesData;
    private ObservableList<Pair<String, String>> analyticsData;
    private ObservableList<TextRecord> recordsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize operations combo box
        operationComboBox.setItems(FXCollections.observableArrayList(
                "Find Matches", "Count Matches", "Replace Matches", "Get Match Details"
        ));
        operationComboBox.setValue("Find Matches");

        // Initialize spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        minCountSpinner.setValueFactory(valueFactory);

        // Initialize table columns for matches
        matchTextColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Match").toString()));
        startIndexColumn.setCellValueFactory(data -> new SimpleObjectProperty<>((Integer) data.getValue().get("Start")));
        endIndexColumn.setCellValueFactory(data -> new SimpleObjectProperty<>((Integer) data.getValue().get("End")));
        // Initialize table columns for analytics
        keyColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().toString()));

        // Initialize table columns for records
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        recordLabelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        recordContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        // Initialize observable lists
        matchesData = FXCollections.observableArrayList();
        matchesTableView.setItems(matchesData);

        analyticsData = FXCollections.observableArrayList();
        analyticsTableView.setItems(analyticsData);

        recordsData = FXCollections.observableArrayList();
        recordsTableView.setItems(recordsData);

        // Add listener for record selection
        recordsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                recordIdField.setText(newSelection.getId());
                recordLabelField.setText(newSelection.getLabel());
                recordContentArea.setText(newSelection.getContent());
            }
        });

        // Set status
        setStatus("Ready");
    }

    // File Menu Handlers
   @FXML
    protected void handleOpenFile() {
        LoggerUtil.info("User initiated file open action.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File selectedFile = fileChooser.showOpenDialog(inputTextArea.getScene().getWindow());
        if (selectedFile != null) {
            try {
                LoggerUtil.info("User selected file: " + selectedFile.getAbsolutePath());
                String content = fileProcessingService.readFile(selectedFile.toPath());
                inputTextArea.setText(content);
                setStatus("File loaded: " + selectedFile.getName());
                LoggerUtil.info("File loaded successfully: " + selectedFile.getName());
            } catch (IOException e) {
                LoggerUtil.error("Error loading file: " + selectedFile.getName(), e);
                showError("Error loading file", e.getMessage());
            }
        } else {
            LoggerUtil.info("User canceled file open action.");
        }
    }

    @FXML
protected void handleSaveFile() {
    LoggerUtil.info("User initiated file save action.");
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Text File");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
    );

    File selectedFile = fileChooser.showSaveDialog(outputTextArea.getScene().getWindow());
    if (selectedFile != null) {
        try {
            LoggerUtil.info("User selected file: " + selectedFile.getAbsolutePath());
            String content = outputTextArea.getText();
            fileProcessingService.writeFile(selectedFile.toPath(), content);
            setStatus("File saved: " + selectedFile.getName());
            LoggerUtil.info("File saved successfully: " + selectedFile.getName());
        } catch (IOException e) {
            LoggerUtil.error("Error saving file: " + selectedFile.getName(), e);
            showError("Error saving file", e.getMessage());
        }
    } else {
        LoggerUtil.info("User canceled file save action.");
    }
}
    @FXML
    protected void handleExit() {
        LoggerUtil.info("User initiated application exit.");
        Platform.exit();
        LoggerUtil.info("Application exited successfully.");
    }

    // Edit Menu Handlers
    @FXML
    protected void handleClear() {
        LoggerUtil.info("User initiated clear action.");
        inputTextArea.clear();
        outputTextArea.clear();
        matchesData.clear();
        setStatus("All cleared");
        LoggerUtil.info("Clear action completed successfully.");
    }

    @FXML
    protected void handleCopyResults() {
        LoggerUtil.info("User initiated copy results action.");
        try {
            outputTextArea.selectAll();
            outputTextArea.copy();
            outputTextArea.deselect();
            setStatus("Results copied to clipboard");
            LoggerUtil.info("Results copied to clipboard successfully.");
        } catch (Exception e) {
            LoggerUtil.error("Error copying results to clipboard", e);
            showError("Copy Error", e.getMessage());
        }
    }

    // Help Menu Handlers
    @FXML
    protected void handleAbout() {
        LoggerUtil.info("User opened the About dialog.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About DataFlow");
        alert.setHeaderText("DataFlow - Regex & Text Processing Tool");
        alert.setContentText("Version 1.0\n\nA powerful tool for regex operations, text processing, and data management.\n\nDeveloper: Abdul Baaki Hudu");
        alert.showAndWait();
        LoggerUtil.info("About dialog closed.");
    }

    // Regex Operations Tab Handlers
    @FXML
    protected void handleValidatePattern() {
        LoggerUtil.info("User initiated regex pattern validation.");
        String pattern = regexPatternField.getText();
        if (pattern.isEmpty()) {
            LoggerUtil.warn("Validation failed: No regex pattern provided.");
            showWarning("Validation", "Please enter a regex pattern");
            return;
        }

        boolean isValid = regexService.isValidRegex(pattern);
        if (isValid) {
            LoggerUtil.info("Regex pattern is valid: " + pattern);
            showInfo("Validation", "The regex pattern is valid");
        } else {
            LoggerUtil.warn("Regex pattern is invalid: " + pattern);
            showWarning("Validation", "The regex pattern is invalid");
        }
    }

@FXML
protected void handleExecuteOperation() {
    LoggerUtil.info("User initiated regex operation execution.");
    String pattern = regexPatternField.getText();
    String operation = operationComboBox.getValue();
    String replacement = replacementField.getText();
    String input = inputTextArea.getText();

    if (pattern.isEmpty()) {
        LoggerUtil.warn("Regex operation failed: No regex pattern provided.");
        showWarning("Error", "Please enter a regex pattern");
        return;
    }

    if (!regexService.isValidRegex(pattern)) {
        LoggerUtil.warn("Regex operation failed: Invalid regex pattern.");
        showWarning("Error", "The regex pattern is invalid");
        return;
    }

    if (input.isEmpty()) {
        LoggerUtil.warn("Regex operation failed: No input text provided.");
        showWarning("Error", "Please enter input text or load a file");
        return;
    }

    setStatus("Processing...");
    showProgress(true);

    executorService.submit(() -> {
        try {
            switch (operation) {
                case "Find Matches":
                    LoggerUtil.info("Executing 'Find Matches' operation.");
                    List<String> matches = regexService.findMatches(input, pattern);
                    Platform.runLater(() -> {
                        outputTextArea.setText(String.join("\n", matches));
                        setStatus("Found " + matches.size() + " matches");
                        LoggerUtil.info("Found " + matches.size() + " matches.");
                    });
                    break;

                case "Count Matches":
                    LoggerUtil.info("Executing 'Count Matches' operation.");
                    long count = regexService.countMatches(input, pattern);
                    Platform.runLater(() -> {
                        outputTextArea.setText("Total matches: " + count);
                        setStatus("Counted " + count + " matches");
                        LoggerUtil.info("Counted " + count + " matches.");
                    });
                    break;

                case "Replace Matches":
                    LoggerUtil.info("Executing 'Replace Matches' operation.");
                    if (replacement == null || replacement.isEmpty()) {
                        Platform.runLater(() -> showWarning("Error", "Please enter replacement text"));
                        LoggerUtil.warn("Replacement operation failed: No replacement text provided.");
                        return;
                    }
                    String modified = regexService.replaceMatches(input, pattern, replacement);
                    Platform.runLater(() -> {
                        outputTextArea.setText(modified);
                        setStatus("Replacement completed");
                        LoggerUtil.info("Replacement operation completed successfully.");
                    });
                    break;

                case "Get Match Details":
                    LoggerUtil.info("Executing 'Get Match Details' operation.");
                    List<Map<String, Object>> details = regexService.getMatchDetails(input, pattern);
                    Platform.runLater(() -> {
                        matchesData.clear();
                        matchesData.addAll(details);
                        setStatus("Found " + details.size() + " match details");
                        LoggerUtil.info("Found " + details.size() + " match details.");
                    });
                    break;

                default:
                    LoggerUtil.warn("Unknown operation: " + operation);
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                showError("Operation Error", e.getMessage());
                LoggerUtil.error("Error in regex operation", e);
            });
        } finally {
            Platform.runLater(() -> showProgress(false));
        }
    });
}

    @FXML
    protected void handleLoadSample() {
        LoggerUtil.info("User initiated load sample action.");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample.txt")) {
            if (inputStream == null) {
                LoggerUtil.warn("Load sample failed: Sample file not found.");
                showWarning("Resource Error", "Sample file not found");
                return;
            }

            String sampleText = new String(inputStream.readAllBytes());
            inputTextArea.setText(sampleText);
            setStatus("Sample text loaded");
            LoggerUtil.info("Sample text loaded successfully.");
        } catch (IOException e) {
            LoggerUtil.error("Error loading sample text", e);
            showError("Error", "Error loading sample text: " + e.getMessage());
        }
    }

    @FXML
    protected void handleClearInput() {
        LoggerUtil.info("User initiated input clear action.");
        inputTextArea.clear();
        setStatus("Input cleared");
        LoggerUtil.info("Input cleared successfully.");
    }

    // Text Analytics Tab Handlers
    @FXML
    protected void handleTextSummary() {
        LoggerUtil.info("User initiated text summary generation.");
        String input = inputTextArea.getText();
        if (input.isEmpty()) {
            LoggerUtil.warn("Text summary failed: No input text provided.");
            showWarning("Error", "Please enter input text or load a file");
            return;
        }

        setStatus("Generating summary...");
        showProgress(true);

        executorService.submit(() -> {
            try {
                Map<String, Long> summary = streamProcessingService.summarizeText(input);
                LoggerUtil.info("Text summary generated successfully.");

                Platform.runLater(() -> {
                    analyticsResultsArea.clear();
                    analyticsData.clear();

                    StringBuilder sb = new StringBuilder();
                    summary.forEach((key, value) -> {
                        sb.append(key).append(": ").append(value).append("\n");
                        analyticsData.add(new Pair<>(key, value.toString()));
                    });

                    analyticsResultsArea.setText(sb.toString());
                    setStatus("Summary generated");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Analysis Error", e.getMessage());
                    LoggerUtil.error("Error generating text summary", e);
                });
            } finally {
                Platform.runLater(() -> showProgress(false));
            }
        });
    }

    @FXML
    protected void handleWordFrequency() {
        LoggerUtil.info("User initiated word frequency calculation.");
        String input = inputTextArea.getText();
        if (input.isEmpty()) {
            LoggerUtil.warn("Word frequency calculation failed: No input text provided.");
            showWarning("Error", "Please enter input text or load a file");
            return;
        }

        int minCount = minCountSpinner.getValue();
        setStatus("Calculating word frequency...");
        showProgress(true);

        executorService.submit(() -> {
            try {
                Map<String, Long> freqMap = streamProcessingService.computeWordFrequency(input);
                LoggerUtil.info("Word frequency calculated successfully.");

                // Filter by minimum count
                Map<String, Long> filteredMap = freqMap.entrySet().stream()
                        .filter(entry -> entry.getValue() >= minCount)
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        ));

                Platform.runLater(() -> {
                    analyticsResultsArea.clear();
                    analyticsData.clear();

                    StringBuilder sb = new StringBuilder();
                    filteredMap.forEach((word, count) -> {
                        sb.append(word).append(": ").append(count).append("\n");
                        analyticsData.add(new Pair<>(word, count.toString()));
                    });

                    analyticsResultsArea.setText(sb.toString());
                    setStatus("Found " + filteredMap.size() + " unique words with count >= " + minCount);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Analysis Error", e.getMessage());
                    LoggerUtil.error("Error calculating word frequency", e);
                });
            } finally {
                Platform.runLater(() -> showProgress(false));
            }
        });
    }

    @FXML
    protected void handleFilterLines() {
        LoggerUtil.info("User initiated line filtering action.");
        String input = inputTextArea.getText();
        String pattern = filterPatternField.getText();

        if (input.isEmpty()) {
            LoggerUtil.warn("Line filtering failed: No input text provided.");
            showWarning("Error", "Please enter input text or load a file");
            return;
        }

        if (pattern.isEmpty()) {
            LoggerUtil.warn("Line filtering failed: No filter pattern provided.");
            showWarning("Error", "Please enter a filter pattern");
            return;
        }

        setStatus("Filtering lines...");
        showProgress(true);

        executorService.submit(() -> {
            try {
                List<String> matchedLines = streamProcessingService.filterLinesByPattern(input, pattern);
                LoggerUtil.info("Line filtering completed successfully. Found " + matchedLines.size() + " matching lines.");

                Platform.runLater(() -> {
                    analyticsResultsArea.setText(String.join("\n", matchedLines));
                    setStatus("Found " + matchedLines.size() + " matching lines");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Filter Error", e.getMessage());
                    LoggerUtil.error("Error filtering lines", e);
                });
            } finally {
                Platform.runLater(() -> showProgress(false));
            }
        });
    }

    // Data Management Tab Handlers
    @FXML
    protected void handleAddRecord() {
        LoggerUtil.info("User initiated add record action.");
        String id = recordIdField.getText();
        String label = recordLabelField.getText();
        String content = recordContentArea.getText();

        if (id.isEmpty() || label.isEmpty()) {
            LoggerUtil.warn("Add record failed: ID and Label are required.");
            showWarning("Validation Error", "ID and Label are required");
            return;
        }

        TextRecord record = new TextRecord(id, label, content);
        boolean added = dataManagementService.addRecord(record);

        if (added) {
            LoggerUtil.info("Record added successfully: " + id);
            refreshRecordsTable();
            clearRecordFields();
            setStatus("Record added: " + id);
        } else {
            LoggerUtil.warn("Add record failed: Duplicate ID " + id);
            showWarning("Duplicate ID", "A record with ID " + id + " already exists");
        }
    }

    @FXML
    protected void handleUpdateRecord() {
        LoggerUtil.info("User initiated update record action.");
        TextRecord selectedRecord = recordsTableView.getSelectionModel().getSelectedItem();
        String id = recordIdField.getText();

        if (selectedRecord == null && (id.isEmpty() || dataManagementService.getRecord(id) == null)) {
            LoggerUtil.warn("Update record failed: No record selected or invalid ID.");
            showWarning("Selection Error", "Please select a record to update or enter an existing ID");
            return;
        }

        String recordId = id;
        if (selectedRecord != null) {
            recordId = selectedRecord.getId();
        }

        TextRecord record = new TextRecord(
                recordId,
                recordLabelField.getText(),
                recordContentArea.getText()
        );

        boolean updated = dataManagementService.updateRecord(record);
        if (updated) {
            LoggerUtil.info("Record updated successfully: " + recordId);
            refreshRecordsTable();
            setStatus("Record updated: " + recordId);
        } else {
            LoggerUtil.warn("Update record failed: Record not found for ID " + recordId);
            showWarning("Update Error", "Failed to update record. Record not found.");
        }
    }

    @FXML
    protected void handleDeleteRecord() {
        LoggerUtil.info("User initiated delete record action.");
        TextRecord selectedRecord = recordsTableView.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            LoggerUtil.warn("Delete record failed: No record selected.");
            showWarning("Selection Error", "Please select a record to delete");
            return;
        }

        String id = selectedRecord.getId();
        boolean deleted = dataManagementService.deleteRecord(id);

        if (deleted) {
            LoggerUtil.info("Record deleted successfully: " + id);
            refreshRecordsTable();
            clearRecordFields();
            setStatus("Record deleted: " + id);
        } else {
            LoggerUtil.warn("Delete record failed: Record not found for ID " + id);
            showWarning("Delete Error", "Failed to delete record");
        }
    }

    @FXML
    protected void handleClearRecords() {
        LoggerUtil.info("User initiated clear all records action.");
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear All Records");
        confirmation.setHeaderText("Clear All Records");
        confirmation.setContentText("Are you sure you want to delete all records?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Collection<TextRecord> allRecords = dataManagementService.getAllRecords();
            for (TextRecord record : new ArrayList<>(allRecords)) {
                dataManagementService.deleteRecord(record.getId());
            }
            LoggerUtil.info("All records cleared successfully.");
            refreshRecordsTable();
            clearRecordFields();
            setStatus("All records cleared");
        } else {
            LoggerUtil.info("User canceled clear all records action.");
        }
    }

    // Utility methods
    private void refreshRecordsTable() {
        recordsData.clear();
        recordsData.addAll(dataManagementService.getAllRecords());
    }

    private void clearRecordFields() {
        recordIdField.clear();
        recordLabelField.clear();
        recordContentArea.clear();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showProgress(boolean show) {
        progressBar.setProgress(show ? -1 : 0);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
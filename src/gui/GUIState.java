package gui;

import gui.components.StateViewer;

import javax.swing.*;

public class GUIState {
    private static final GUIState guiState = new GUIState();

    private final StateViewer stateViewer = StateViewer.getInstance();
    private String sourceCodePath;
    private String testCodePath;

    public String getSourceCodePath() {
        return sourceCodePath;
    }

    public void setSourceCodePath(String sourceCodePath) {
        this.sourceCodePath = sourceCodePath;
        stateViewer.setText();
    }

    public String getTestCodePath() {
        return testCodePath;
    }

    public void setTestCodePath(String testCodePath) {
        this.testCodePath = testCodePath;
        stateViewer.setText();
    }

    public void clearCodePath() {
        setSourceCodePath(null);
        setTestCodePath(null);
        stateViewer.clearText();
    }

    public JPanel getPanel() {
        return stateViewer.getPanel();
    }

    public static GUIState getInstance() {
        return guiState;
    }
}

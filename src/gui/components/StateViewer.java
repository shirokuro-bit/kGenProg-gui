package gui.components;

import gui.GUIState;

import javax.swing.*;

public class StateViewer extends AbstractComponent {
    private static final StateViewer STATE_VIEWER = new StateViewer();
    private JLabel sourceCodePathLabel;
    private JLabel testCodePathLabel;

    public void setText() {
        GUIState guiState = GUIState.getInstance();
        sourceCodePathLabel.setText(guiState.getSourceCodePath());
        testCodePathLabel.setText(guiState.getTestCodePath());
    }

    public void clearText() {
        sourceCodePathLabel.setText("");
        testCodePathLabel.setText("");
    }

    public static StateViewer getInstance() {
        return STATE_VIEWER;
    }

    @Override
    public void buildComponent() {
        sourceCodePathLabel = new JLabel("codePath");
        testCodePathLabel = new JLabel("testPath");

        panel.add(sourceCodePathLabel);
        panel.add(testCodePathLabel);
    }
}

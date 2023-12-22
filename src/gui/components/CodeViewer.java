package gui.components;

import javax.swing.*;
import java.awt.*;

public class CodeViewer {
    private static final CodeViewer codeViewer = new CodeViewer();

    private final JPanel panel;
    private final JTextArea sourceCodeArea;
    private final JTextArea fixCodeArea;

    private CodeViewer() {
        panel = new JPanel();
        sourceCodeArea = new JTextArea(30, 40);
        fixCodeArea = new JTextArea(30, 40);

        panel.add(attachScrollbar(sourceCodeArea), BorderLayout.CENTER);
        panel.add(attachScrollbar(fixCodeArea), BorderLayout.EAST);
    }

    JScrollPane attachScrollbar(Component component) {
        return new JScrollPane(component);
    }

    public void clearText() {
        setText("", "");
    }

    public void setSourceCode(String sourceCode) {
        sourceCodeArea.setText(sourceCode);
    }

    public void setFixCode(String fixCode) {
        fixCodeArea.setText(fixCode);
    }

    public void setText(String sourceCode, String fixCode) {
        sourceCodeArea.setText(sourceCode);
        fixCodeArea.setText(fixCode);
    }

    public JPanel getPanel() {
        return panel;
    }

    public Dimension getPreferredSize() {
        return sourceCodeArea.getPreferredSize();
    }

    public static CodeViewer getCodeViewer() {
        return codeViewer;
    }
}

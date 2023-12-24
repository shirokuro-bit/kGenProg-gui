package gui.components;

import javax.swing.*;
import java.awt.*;

import static gui.components.GUIUtil.attachScrollbar;

public class CodeViewer extends AbstractComponent {
    private static final CodeViewer codeViewer = new CodeViewer();
    private final JTextArea sourceCodeArea = new JTextArea(30, 40);
    private final JTextArea fixCodeArea = new JTextArea(30, 40);

    private CodeViewer() {}

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

    public Dimension getPreferredSize() {
        return sourceCodeArea.getPreferredSize();
    }

    public static CodeViewer getCodeViewer() {
        return codeViewer;
    }

    @Override
    public void buildComponent() {
        jPanel.add(attachScrollbar(sourceCodeArea), BorderLayout.CENTER);
        jPanel.add(attachScrollbar(fixCodeArea), BorderLayout.EAST);
    }
}

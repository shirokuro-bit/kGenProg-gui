package gui.components;

import javax.swing.*;

public abstract class AbstractComponent {
    protected final JPanel jPanel = new JPanel();
    protected abstract void buildComponent();
    public JPanel getJPanel() {
        buildComponent();
        return jPanel;
    }
}

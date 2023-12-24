package gui.components;

import javax.swing.*;

public abstract class AbstractComponent {
    protected final JPanel panel = new JPanel();
    public abstract void buildComponent();

    public JPanel getPanel() {
        buildComponent();
        return panel;
    }
}

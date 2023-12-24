package gui.components;

import javax.swing.*;
import java.awt.*;

public class GUIUtil {
    static JScrollPane attachScrollbar(Component component) {
        return new JScrollPane(component);
    }
}

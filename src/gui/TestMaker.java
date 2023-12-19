package gui;

import utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMaker extends JDialog {

    private final String targetClassName;
    private JPanel contentPane;
    private JPanel createTestPanel;
    private JButton buttonOK;
    private JButton buttonCancel;
    private final List<TestValueField> testValueFields = new ArrayList<>();

    public TestMaker(Frame owner, Class<?> targetClass) {
        super(owner, targetClass.getName(), true);
        targetClassName = targetClass.getName();
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        createTestPanel.setLayout(new BoxLayout(createTestPanel, BoxLayout.Y_AXIS));

        for (Method method : targetClass.getDeclaredMethods()) {
            TestValueField testValueField = new TestValueField(method);
            testValueFields.add(testValueField);
            createTestPanel.add(testValueField.getPanel());

            JButton addButton = new JButton("追加");
            addButton.addActionListener(e -> {
                int eventComponentIndex = Arrays.asList(createTestPanel.getComponents()).indexOf(e.getSource());

                TestValueField testValueField1 = new TestValueField(method);
                testValueFields.add(testValueField1);
                createTestPanel.add(testValueField1.getPanel(), eventComponentIndex);
                revalidate();
                pack();
            });
            createTestPanel.add(addButton);
        }

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // X をクリックしたとき、 onCancel() を呼ぶ
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // ESCAPE で onCancel() を呼ぶ
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        createTest();
        dispose();
    }

    private void onCancel() {
        // 必要に応じてここにコードを追加
        dispose();
    }

    private void createTest() {
        List<String> testDataList = new ArrayList<>();
        for (int i = 0; i < testValueFields.size(); i++) {
            TestValueField testValueField = testValueFields.get(i);
            String methodName = testValueField.getMethodName();
            String testID = "%02d".formatted(i);
            String testMethodName = methodName + testID;
            String paramValues = String.join(", ", testValueField.getParamValues());

            String testTemp =
                    STR."""
                        @Test
                        public void \{testMethodName}() {
                            assertEquals(\{testValueField.getReturnValue()}, new \{targetClassName}().\{methodName}(\{paramValues}));
                        }
                    """;

            testDataList.add(testTemp);
        }

        String temp =
                STR."""
                import static org.junit.Assert.assertEquals;
                import org.junit.Test;

                public class CloseToZeroTest {
                \{String.join("\n", testDataList)}
                }
                """;

        System.out.println(temp);
        try {
            FileUtils.writeText("C:\\share\\JetBrains\\IdeaProjects\\kGenProg\\example\\CloseToZeroTest.java", temp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class TestValueField {
        private final Method method;
        private final String methodData;
        private final JPanel panel = new JPanel();
        private final JTextField returnJTextField;
        private final List<JTextField> paramJTextFields = new ArrayList<>();

        public TestValueField(Method method) {
            this.method = method;
            List<String> paramTypes = Arrays.stream(method.getParameterTypes()).map(Class::getName).toList();
            methodData = String.format("%s(%s):%s\n", method.getName(), String.join(", ", paramTypes), method.getReturnType().getName());

            panel.add(new JLabel(methodData));

            panel.add(new JLabel("引数"));

            paramTypes.forEach(s -> paramJTextFields.add(new JTextField(4)));
            paramJTextFields.forEach(panel::add);

            panel.add(new JLabel("戻り値"));
            returnJTextField = new JTextField(4);
            panel.add(returnJTextField);
        }

        public String getMethodName() {
            return method.getName();
        }

        public String getReturnValue() {
            return returnJTextField.getText();
        }

        public List<String> getParamValues() {
            return paramJTextFields.stream().map(JTextField::getText).toList();
        }

        public JPanel getPanel() {
            return panel;
        }
    }
}

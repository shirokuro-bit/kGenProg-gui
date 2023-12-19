package gui;

import utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GUI {
    JFrame frame;
    JPanel panel1;
    JPanel panel2;
    JTree directoryTree;
    JTextArea codeArea;
    JTextArea fixCodeArea;
    JButton fixButton;
    JButton createTestButton;

    JLabel codePathLabel;
    JLabel testPathLabel;

    String codePath;
    String testPath;

    public GUI() {
        frame = new JFrame("kGenProg");
        frame.setBounds(100, 100, 1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        menubar.add(fileMenu);

        JMenuItem openFileMItem = new JMenuItem("Open File");
        openFileMItem.addActionListener(e -> openFileChooser());

        JMenuItem openFolderMItem = new JMenuItem("Open Folder");
        openFolderMItem.addActionListener(e -> openFolder());

        JMenuItem closeEditorMItem = new JMenuItem("Close Editor");
        closeEditorMItem.addActionListener(e -> closeEditor());

        JMenuItem exitMItem = new JMenuItem("Exit");
        exitMItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openFileMItem);
        fileMenu.add(openFolderMItem);
        fileMenu.add(closeEditorMItem);
        fileMenu.add(exitMItem);

        JToolBar toolBar = new JToolBar();
        createTestButton = new JButton("テスト作成");
        createTestButton.addActionListener(e -> {
            if (codePath == null) {
                messageDialog("ファイルが指定されていません");
                return;
            }
            fixCodeArea.setText("");
            Class<?> targetClass = new FileUtils().compileAndRun(codePath);

            JDialog dialog = new TestMaker(frame, targetClass);
            dialog.pack();
            dialog.setVisible(true);
        });

        fixButton = new JButton("変換");
        fixButton.addActionListener(e -> {
            if (codePath == null) {
                messageDialog("ファイルが指定されていません");
                return;
            }
            fixCodeArea.setText("");
            fixCode();
        });

        toolBar.add(createTestButton);
        toolBar.add(fixButton);

        panel1 = new JPanel();
        panel2 = new JPanel();

        // ディレクトリツリーのルートノードを作成
        String rootPath = System.getProperty("user.dir");
        File rootDirectory = new File(rootPath);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDirectory.getName());

        File[] files = rootDirectory.listFiles();
        if (files != null) {
            Stream<File> directories = Arrays.stream(files)
                    .filter(File::isDirectory)
                    .sorted(Comparator.comparing(File::getName));

            Stream<File> regularFiles = Arrays.stream(files)
                    .filter(file -> !file.isDirectory())
                    .sorted(Comparator.comparing(File::getName, (name1, name2) -> {
                        String extension1 = name1.substring(name1.lastIndexOf("."));
                        String extension2 = name2.substring(name2.lastIndexOf("."));
                        int extensionCompare = extension1.compareToIgnoreCase(extension2);
                        if (extensionCompare == 0) {
                            return name1.compareToIgnoreCase(name2);
                        }
                        return extensionCompare;
                    }));

            // ディレクトリとファイルを結合して種類順にソートした一覧を作成
            File[] sortedFiles = Stream.concat(directories, regularFiles).toArray(File[]::new);

            // ディレクトリをルートにしてツリーを構築
            createFileDirectoryTree(root, sortedFiles);
        }

        codeArea = new JTextArea(30, 40);
        codeArea.getSize();

        fixCodeArea = new JTextArea(30, 40);

        // ディレクトリツリーの作成
        directoryTree = new JTree(root);
        directoryTree.setPreferredSize(new Dimension(200, codeArea.getPreferredSize().height));
        directoryTree.addTreeSelectionListener(e -> {
            String[] array =  e.getPath().toString().replaceAll("[\\[\\]\\s]", "").split(",");
            File treeSelectPath = new File(Path.of(new File(rootPath).getParentFile().getPath(), array).toString());
            if (treeSelectPath.isFile()) {
                try {
                    codePath = treeSelectPath.getPath();
                    testPath = codePath.replaceAll("\\.java$", "Test.java");
                    codeArea.setText(FileUtils.loadText(codePath));
                    codePathLabel.setText(codePath);
                    testPathLabel.setText(testPath);
                } catch (IOException ex) {
                    messageDialog(ex.fillInStackTrace().toString());
                }
            }
        });

        codePathLabel = new JLabel("codePath");
        testPathLabel = new JLabel("testPath");

        panel1.add(directoryTree, BorderLayout.WEST);
        panel1.add(attachScrollbar(codeArea), BorderLayout.CENTER);
        panel1.add(attachScrollbar(fixCodeArea), BorderLayout.EAST);
        panel2.add(codePathLabel, BorderLayout.PAGE_START);
        panel2.add(testPathLabel, BorderLayout.PAGE_END);

        Container contentPane = frame.getContentPane();
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(panel1, BorderLayout.CENTER);
        contentPane.add(panel2, BorderLayout.SOUTH);

        frame.setJMenuBar(menubar);
        frame.setVisible(true);
    }

    void messageDialog(String message) {
        JLabel label = new JLabel(message);
        JOptionPane.showMessageDialog(panel1, label);
    }

    JScrollPane attachScrollbar(Component component) {
        return new JScrollPane(component);
    }

    void closeEditor() {
        codePath = null;
        codeArea.setText("");
        fixCodeArea.setText("");
        codePathLabel.setText("");
        testPathLabel.setText("");
    }

    void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Javaファイル", "java"));
        int selected = fileChooser.showOpenDialog(frame);
        switch (selected) {
            case JFileChooser.APPROVE_OPTION -> openFile(fileChooser.getSelectedFile());
            case JFileChooser.ERROR_OPTION -> messageDialog("エラー又は取消しがありました");
        }
    }

    void openFile(File file) {
        // TODO: 2023/11/30 0030 Testコードが分離されている場合の考慮が必要
        try {
            codePath = file.getPath();
            testPath = codePath.replaceAll("\\.java$", "Test.java");
            codeArea.setText(FileUtils.loadText(codePath));
            codePathLabel.setText(codePath);
            testPathLabel.setText(testPath);
        } catch (IOException e) {
            messageDialog(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void openFolder() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selected = fileChooser.showOpenDialog(frame);
        if (selected == JFileChooser.ERROR_OPTION) {
            messageDialog("エラー又は取消しがありました");
        }
    }

    void createFileDirectoryTree(DefaultMutableTreeNode rootNode, File[] files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
            rootNode.add(node);
            if (file.isDirectory()) {
                createFileDirectoryTree(node, file.listFiles());
            }
        }
    }

    void fixCode() {
        String[] args = {"java", "-jar", "./lib/kGenProg-1.8.2.jar", "-r", "./", "-s", codePath, "-t", testPath, "--patch-output"};
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process process = processBuilder.start();

            boolean result = process.waitFor(10, TimeUnit.SECONDS);
            if (result == false) {
                process.destroy();
                System.out.println("timeout");
                return;
            }
            System.out.println("Result : " + result);

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    log.append(line);
                }

                // 正規表現を使用してGenerated variantsの行から数値を抽出する
                Pattern pattern = Pattern.compile("Generated variants = (\\d+)");
                Matcher matcher = pattern.matcher(log);

                if (matcher.find()) {
                    String generatedVariants = matcher.group(1);
                    System.out.println("Generated variants: " + generatedVariants);
                    fixCodeArea.setText(FileUtils.loadText("./kgenprog-out/patch-v" + generatedVariants + "/CloseToZero.java"));
                } else {
                    System.out.println("Generated variants not found.");
                }
            }

            try (BufferedReader errorBufferedreader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorBufferedreader.readLine()) != null) {
                    System.err.println(errorLine);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

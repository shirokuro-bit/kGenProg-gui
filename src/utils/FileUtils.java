package utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String loadText(String path) throws IOException {
        return Files.readString(Path.of(path), StandardCharsets.UTF_8);
    }

    public Class<?> compileAndRun(String filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, filePath);

        if (compilationResult == 0) {
            // コンパイルされたクラスファイルのロード
            File file = new File(filePath.substring(0, filePath.lastIndexOf(".")) + ".class");
            return new FileClassLoader().loadClassFile(file);
        } else {
            System.out.println("コンパイルエラーが発生しました。");
        }
        return null;
    }

    public static void writeText(String path, String data) throws IOException {
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(data);
        fileWriter.close();
    }
}

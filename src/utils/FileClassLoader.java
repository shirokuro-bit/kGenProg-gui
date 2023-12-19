package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileClassLoader extends ClassLoader{
    private static final int BUF_SIZE = 1024;
    public Class<?> loadClassFile(File classFile){
        try (FileInputStream in = new FileInputStream(classFile)){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[BUF_SIZE];
            int len = in.read(buf);
            while(len != -1) {
                out.write(buf, 0, len);
                len = in.read(buf);
            }
            byte[] loadedData = out.toByteArray();
            return defineClass(null, loadedData, 0, loadedData.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
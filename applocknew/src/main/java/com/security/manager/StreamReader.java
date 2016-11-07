package com.security.manager;

import java.io.*;

/**
 * Created by superjoy on 2014/8/21.
 */
public class StreamReader extends Thread {
    private InputStream is;
    public StreamReader(InputStream is) {
        this.is = is;
    }
    String content;

    public void run() {
        content = read(is);
    }

    public void start(Process process) throws InterruptedException {
        start();
        process.waitFor();
        join();
    }

    public String getResult() {
        return content;
    }

    // 读取输入流
    private static String read(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer content = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

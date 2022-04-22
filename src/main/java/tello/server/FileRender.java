package tello.server;

import java.io.*;

public class FileRender {
    public static String renderHTMLFiles(String filePath) throws IOException {
        BufferedReader br = new BufferedReader( new FileReader(filePath) );
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        String
    }
}

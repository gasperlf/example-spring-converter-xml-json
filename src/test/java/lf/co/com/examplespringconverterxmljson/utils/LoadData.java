package lf.co.com.examplespringconverterxmljson.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoadData {

    public static String loadFile(String filaName) {
        InputStream input = LoadData.class.getClassLoader().getResourceAsStream(filaName);
        try (Scanner s = new java.util.Scanner(input, StandardCharsets.UTF_8.name())) {
            s.useDelimiter("\\A");
            String body = s.next();

            if (body == null || body.length() == 0) {
                throw new IllegalArgumentException("File name " + filaName + " don´t have content");
            }
            return body;
        }
    }
}

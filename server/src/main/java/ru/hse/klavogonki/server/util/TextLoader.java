package ru.hse.klavogonki.server.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TextLoader {
    private final List<String> texts = new ArrayList<>();

    public TextLoader() {
        ClassLoader classLoader = TextLoader.class.getClassLoader();
        URL url = classLoader.getResource("texts.txt");
        if (url == null) {
            throw new RuntimeException("No texts.txt file provided!");
        }
        String filename = url.getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                texts.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getText() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return texts.get((random.nextInt() % texts.size() + texts.size()) % texts.size());
    }
}

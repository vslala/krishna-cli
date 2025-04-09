package org.main.commands.krishna.agentrunner;

import lombok.extern.slf4j.Slf4j;
import org.main.commands.krishna.KrishnaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class LoadPrompt {

    public static String byName(String name) {
        String resourcePath = "agent_prompts/" + name + "/system.md";
        InputStream inputStream = LoadPrompt.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new KrishnaException("Resource not found: " + resourcePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new KrishnaException("Error reading resource: " + resourcePath, e);
        }

        return content.toString();
    }
}

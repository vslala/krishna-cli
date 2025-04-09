package org.main.chatbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static String parsePythonCode(String text) {
        String regex = "(?s)```python\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No Python code block found.";
        }
    }

    public static String parseJSONBlock(String text) {
        String regex = "(?s)```json\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No Json code block found.";
        }
    }

    public static String parseTextBlock(String text) {
        String regex = "(?s)```text\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No Json code block found.";
        }
    }
}

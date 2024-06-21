package org.chatbot.commands;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayInputStream;

class KrishnaTest {

    @Test
    void should_call_the_chatbot_to_summarise_the_text_using_the_provided_prompt() {
        var inputStream = new ByteArrayInputStream("I met a beautiful women in a coffee shop".getBytes());
        System.setIn(inputStream);
        new CommandLine(new Krishna()).execute("-p", "extract_book_ideas");
    }

    @Test
    void should_start_conversation() {
        new CommandLine(new Krishna()).execute("-sc");
    }
}

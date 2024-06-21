package org.chatbot.commands;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate-image", description = "Generates an image")
public class ImageGeneration implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        return 0;
    }
}

/*
 * This source file was generated by the Gradle 'init' task
 */
package org.main;

import org.main.commands.krishna.Krishna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class App implements CommandLineRunner, ExitCodeGenerator {
    private final CommandLine.IFactory factory;
    private final Krishna krishnaCommand;
    private int exitCode = 0;

    @Autowired
    public App(CommandLine.IFactory factory, Krishna krishnaCommand) {
        this.factory = factory;
        this.krishnaCommand = krishnaCommand;
    }

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(krishnaCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        System.exit(SpringApplication.exit(app.run(args)));
    }


}

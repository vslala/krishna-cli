package org.main.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class PythonContainerRunner {
    private static final String dockerDir = "/tmp/test_containers";

    private String dockerFileContent() {
        return """
                # Use the official Python image from the Docker Hub
                FROM python:3.9-slim
                                
                # Set the working directory in the container
                WORKDIR /usr/src/app
                                
                # Copy the current directory contents into the container
                COPY . .
                                
                # Install any needed packages specified in requirements.txt
                RUN pip install --no-cache-dir -r requirements.txt
                                
                # Run script.py when the container launches
                CMD ["python", "python_script.py"]
                """;
    }

    public boolean build(String requirements, String pythonCode) {
        try {
            if (!Files.exists(Path.of(dockerDir))) {
                Files.createDirectory(Path.of(dockerDir));
            }

            Path requirementsFilePath = Path.of(dockerDir.concat("/requirements.txt"));
            Path pythonScriptFilePath = Path.of(dockerDir.concat("/python_script.py"));
            Path dockerFilePath = Path.of(dockerDir.concat("/Dockerfile"));

            Files.writeString(requirementsFilePath, requirements);
            Files.writeString(pythonScriptFilePath, pythonCode);
            Files.writeString(dockerFilePath, dockerFileContent());

            ProcessBuilder pb = new ProcessBuilder("docker", "build", "-t", "my-python-app", dockerDir);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Docker build failed with exit code " + exitCode);
                return false;
            }
            return true;
        } catch (IOException | InterruptedException ex) {
            return false;
        }
    }

    public String run() {
        System.out.println("EXECUTING DOCKER CONTAINER");
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "run", "--rm", "my-python-app");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Capture the output
            String result;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                result = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Docker run failed with exit code " + exitCode);
                return "ERROR:" + result;
            }

            return result;
        } catch (InterruptedException | IOException ex) {
            return "Error: ".concat(ex.getMessage());
        }
    }
}

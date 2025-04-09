package org.main.commands.krishna;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public interface BaseCommand extends Callable<Integer> {
    @SneakyThrows
    default String readStdIn() {
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (reader.ready() && (line = reader.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
        }

        if (!sb.isEmpty()) {
            sb.insert(0, System.lineSeparator()
                    .concat("# PROVIDED CONTEXT")
                    .concat(System.lineSeparator()));
        }

        return sb.toString();
    }
}

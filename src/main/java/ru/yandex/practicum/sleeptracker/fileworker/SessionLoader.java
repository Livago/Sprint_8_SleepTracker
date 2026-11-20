package ru.yandex.practicum.sleeptracker.fileworker;

import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionLoader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public List<SleepingSession> load(String fileName) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {

            return reader.lines()
                    .map(this::parseSession)
                    .toList();
        }
    }

    private SleepingSession parseSession(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная запись о сне: " + line);
        }

        LocalDateTime fallTime = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime wakeTime = LocalDateTime.parse(parts[1].trim(), FORMATTER);

        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(fallTime, wakeTime, quality);
    }
}

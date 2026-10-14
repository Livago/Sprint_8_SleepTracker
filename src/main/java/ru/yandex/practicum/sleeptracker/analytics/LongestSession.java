package ru.yandex.practicum.sleeptracker.analytics;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class LongestSession implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Самая длинная сессия",
                sessions.stream()
                        .map(SleepingSession::getSleepDuration)
                        .max(Comparator.naturalOrder())
                        .map(duration -> duration.toMinutes() + " Минут")
                        .orElse("Самая длинная сессия не найдена")
        );
    }
}

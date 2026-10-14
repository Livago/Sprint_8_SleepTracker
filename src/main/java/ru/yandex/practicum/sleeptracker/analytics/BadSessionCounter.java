package ru.yandex.practicum.sleeptracker.analytics;

import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class BadSessionCounter implements Function<List<SleepingSession>, SleepAnalysisResult>  {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Количество плохих сессий сна",
                sessions.stream()
                        .filter(session -> session.getQuality().equals(SleepQuality.BAD))
                        .count()
        );
    }
}

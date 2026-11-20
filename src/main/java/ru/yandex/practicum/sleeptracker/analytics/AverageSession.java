package ru.yandex.practicum.sleeptracker.analytics;

import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class AverageSession implements Function<List<SleepingSession>, SleepAnalysisResult>  {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Средняя продолжительность сессий",
                sessions.stream()
                        .mapToLong(session -> session.getSleepDuration().toMinutes())
                        .average()
                        .stream()
                        .mapToLong(Math::round)
                        .mapToObj(avg -> avg + " Минут")
                        .findFirst()
                        .orElse("Данные о сне отсутствуют")
        );
    }
}

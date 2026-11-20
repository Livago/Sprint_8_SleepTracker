package ru.yandex.practicum.sleeptracker.analytics;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class ShortestSession implements Function<List<SleepingSession>, SleepAnalysisResult>  {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Самая короткая сессия",
                sessions.stream()
                        .map(SleepingSession::getSleepDuration)
                        .min(Comparator.naturalOrder())
                        .map(duration -> duration.toMinutes() + " Минут")
                        .orElse("Самая короткая сессия не найдена")
        );
    }

}

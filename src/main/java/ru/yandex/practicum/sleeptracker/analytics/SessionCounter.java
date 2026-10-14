package ru.yandex.practicum.sleeptracker.analytics;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class SessionCounter implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Общее количество сессий сна",
                sessions.size()
        );
    }
}

package ru.yandex.practicum.sleeptracker.analytics;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Finds the most common chronotype among night sleep sessions. */
public class Chronotype implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final LocalTime LATE_BEDTIME = LocalTime.of(23, 0);
    private static final LocalTime EARLY_BEDTIME = LocalTime.of(22, 0);
    private static final LocalTime LATE_WAKE_TIME = LocalTime.of(9, 0);
    private static final LocalTime EARLY_WAKE_TIME = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        Map<String, Long> counts = sessions.stream()
                .filter(SleepingSession::isNightSleep)
                .collect(Collectors.groupingBy(this::classifyNight, Collectors.counting()));

        long owlCount = counts.getOrDefault("OWL", 0L);
        long larkCount = counts.getOrDefault("LARK", 0L);
        long pigeonCount = counts.getOrDefault("PIGEON", 0L);

        String result = owlCount > larkCount && owlCount > pigeonCount ? "OWL"
                : larkCount > owlCount && larkCount > pigeonCount ? "LARK"
                : "PIGEON";
        return new SleepAnalysisResult("Хронотип пользователя", result);
    }

    private String classifyNight(SleepingSession session) {
        LocalTime bedtime = session.getFallTime().toLocalTime();
        LocalTime wakeTime = session.getWakeTime().toLocalTime();

        if (bedtime.isAfter(LATE_BEDTIME) && wakeTime.isAfter(LATE_WAKE_TIME)) {
            return "OWL";
        }
        if (bedtime.isBefore(EARLY_BEDTIME) && wakeTime.isBefore(EARLY_WAKE_TIME)) {
            return "LARK";
        }
        return "PIGEON";
    }
}

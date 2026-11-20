package ru.yandex.practicum.sleeptracker.analytics;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class OvernightSessionCounter implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long sleeplessNights = countSleeplessNights(sessions);
        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }

    public long countSleeplessNights(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return 0;
        }

        LocalDate firstNight = firstNightDate(sessions);
        LocalDate lastNight = sessions.stream()
                .map(session -> session.getWakeTime().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(firstNight);

        return Stream.iterate(firstNight, date -> !date.isAfter(lastNight), date -> date.plusDays(1))
                .filter(night -> !hasSleepDuringNight(sessions, night))
                .count();
    }

    private LocalDate firstNightDate(List<SleepingSession> sessions) {
        SleepingSession firstSession = sessions.stream()
                .min(Comparator.comparing(SleepingSession::getFallTime))
                .orElseThrow();
        LocalDate startDate = firstSession.getFallTime().toLocalDate();


        return firstSession.getFallTime().getHour() < 12 ? startDate : startDate.plusDays(1);
    }

    private boolean hasSleepDuringNight(List<SleepingSession> sessions, LocalDate night) {
        LocalDateTime nightStart = night.atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);
        return sessions.stream().anyMatch(session ->
                session.getFallTime().isBefore(nightEnd)
                        && session.getWakeTime().isAfter(nightStart));
    }
}

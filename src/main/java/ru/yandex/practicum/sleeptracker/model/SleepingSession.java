package ru.yandex.practicum.sleeptracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepingSession {

    private final LocalDateTime fallTime;
    private final LocalDateTime wakeTime;
    private final Duration sleepDuration;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime fallTime, LocalDateTime wakeTime, SleepQuality quality) {
        if (wakeTime.isBefore(fallTime)) {
            throw new IllegalArgumentException("Время пробуждения должно быть позже времени засыпания");
        }
        this.fallTime = fallTime;
        this.wakeTime = wakeTime;
        this.sleepDuration = Duration.between(fallTime, wakeTime);
        this.quality = quality;
    }

    public SleepingSession(LocalDateTime fallTime, LocalDateTime wakeTime, Duration ignoredDuration, SleepQuality quality) {
        this(fallTime, wakeTime, quality);
    }

    public LocalDateTime getFallTime() {
        return fallTime;
    }

    public LocalDateTime getWakeTime() {
        return wakeTime;
    }

    public Duration getSleepDuration() {
        return sleepDuration;
    }

    public SleepQuality getQuality() {
        return quality;
    }


    public boolean isNightSleep() {
        boolean crossesMidnight = this.getFallTime().toLocalDate()
                .isBefore(this.getWakeTime().toLocalDate());

        boolean sleptInEarlyMorning = this.getFallTime().getHour() < 6;

        return crossesMidnight || sleptInEarlyMorning;
    }
}

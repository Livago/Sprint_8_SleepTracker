package ru.yandex.practicum.sleeptracker.analytics;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public interface SleepAnalytics<R> {

    String getName();

    R analyze(List<SleepingSession> sessions);
}

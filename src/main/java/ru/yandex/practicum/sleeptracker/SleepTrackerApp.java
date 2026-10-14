package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.analytics.*;
import ru.yandex.practicum.sleeptracker.fileworker.SessionLoader;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;


public class SleepTrackerApp {

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>>
            ANALYTICS = List.of(
            new SessionCounter(),
            new LongestSession(),
            new ShortestSession(),
            new AverageSession(),
            new BadSessionCounter(),
            new OvernightSessionCounter(),
            new Chronotype()
    );

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Использование: java SleepTrackerApp sleep_log.txt");
            return;
        }
        SessionLoader loader = new SessionLoader();
        List<SleepingSession> sessionList = loader.load(args[0]);

        ANALYTICS.forEach(analytics -> {
            SleepAnalysisResult result = analytics.apply(sessionList);

            System.out.println(
                    result.getDescription() + ": " + result.getValue()
            );
        });
    }

}

package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.analytics.*;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepTrackerAppTest {
    private SleepingSession session(String fall, String wake, SleepQuality quality) {
        return new SleepingSession(LocalDateTime.parse(fall), LocalDateTime.parse(wake), quality);
    }

    private Object value(java.util.function.Function<List<SleepingSession>, SleepAnalysisResult> analysis,
                         List<SleepingSession> sessions) {
        return analysis.apply(sessions).getValue();
    }

    @Test
    @DisplayName("Подсчитывает сессии, включая пустой список")
    void countsSessionsIncludingEmptyInput() {
        assertEquals(0L, value(new SessionCounter(), List.of()));
        assertEquals(2, value(new SessionCounter(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.NORMAL))));
    }

    @Test
    @DisplayName("Подсчитывает одну сессию")
    void countsOneSession() {
        assertEquals(1, value(new SessionCounter(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Находит самую короткую и самую длинную сессии")
    void findsShortestAndLongestSession() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:30", SleepQuality.NORMAL));
        assertEquals("30 Минут", value(new ShortestSession(), sessions));
        assertEquals("480 Минут", value(new LongestSession(), sessions));
        assertEquals("480 Минут", value(new ShortestSession(), List.of(sessions.get(0))));
        assertEquals("30 Минут", value(new LongestSession(), List.of(sessions.get(1))));
    }

    @Test
    @DisplayName("Находит самую короткую сессию в списке")
    void findsShortestSessionInLongerList() {
        assertEquals("30 Минут", value(new ShortestSession(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:30", SleepQuality.NORMAL))));
    }

    @Test
    @DisplayName("Находит самую длинную сессию в списке")
    void findsLongestSessionInLongerList() {
        assertEquals("480 Минут", value(new LongestSession(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:30", SleepQuality.NORMAL))));
    }

    @Test
    @DisplayName("Вычисляет среднюю длительность для нескольких и одной сессии")
    void calculatesAverageForSeveralAndSingleSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:00", SleepQuality.NORMAL));
        assertEquals("270 Минут", value(new AverageSession(), sessions));
        assertEquals("480 Минут", value(new AverageSession(), List.of(sessions.getFirst())));
    }

    @Test
    @DisplayName("Округляет среднюю длительность до целых минут")
    void roundsAverageDurationToWholeMinutes() {
        assertEquals("31 Минут", value(new AverageSession(), List.of(
                session("2025-10-01T22:00", "2025-10-01T22:30", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:31", SleepQuality.NORMAL))));
    }

    @Test
    @DisplayName("Подсчитывает сессии с плохим качеством сна")
    void countsBadQualitySessions() {
        assertEquals(0L, value(new BadSessionCounter(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD))));
        assertEquals(2L, value(new BadSessionCounter(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.BAD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.BAD))));
    }

    @Test
    @DisplayName("Подсчитывает плохие сессии среди сессий разного качества")
    void countsBadSessionsAlongsideOtherQuality() {
        assertEquals(1L, value(new BadSessionCounter(), List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.BAD),
                session("2025-10-02T22:00", "2025-10-03T06:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Подсчитывает бессонные ночи с учётом пересечений и первой ночи")
    void countsSleeplessNightsByOverlapAndFirstNightRules() {
        OvernightSessionCounter counter = new OvernightSessionCounter();
        assertEquals(0L, counter.countSleeplessNights(List.of()));
        assertEquals(0L, counter.countSleeplessNights(List.of(
                session("2025-10-01T23:00", "2025-10-02T03:00", SleepQuality.GOOD))));
        assertEquals(0L, counter.countSleeplessNights(List.of(
                session("2025-10-02T02:00", "2025-10-02T07:00", SleepQuality.GOOD))));
        assertEquals(1L, counter.countSleeplessNights(List.of(
                session("2025-10-01T07:00", "2025-10-01T11:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Утренняя сессия покрывает ночной интервал")
    void morningSessionCoversThatMorningNightInterval() {
        assertEquals(0L, new OvernightSessionCounter().countSleeplessNights(List.of(
                session("2025-10-02T02:00", "2025-10-02T07:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Дневная сессия оставляет ночь бессонной")
    void daytimeOnlySessionLeavesNightSleepless() {
        assertEquals(1L, new OvernightSessionCounter().countSleeplessNights(List.of(
                session("2025-10-01T07:00", "2025-10-01T11:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Подсчитывает промежуток между двумя ночами")
    void countsGapBetweenTwoNights() {
        assertEquals(1L, new OvernightSessionCounter().countSleeplessNights(List.of(
                session("2025-10-01T22:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-03T22:00", "2025-10-04T06:00", SleepQuality.GOOD))));
    }

    @Test
    @DisplayName("Определяет хронотип и выбирает голубя при равенстве или отсутствии ночей")
    void classifiesChronotypeAndUsesPigeonForTiesOrNoNights() {
        Chronotype chronotype = new Chronotype();
        assertEquals("OWL", value(chronotype, List.of(
                session("2025-10-01T23:30", "2025-10-02T09:30", SleepQuality.GOOD),
                session("2025-10-02T23:30", "2025-10-03T09:30", SleepQuality.GOOD))));
        assertEquals("LARK", value(chronotype, List.of(
                session("2025-10-01T21:30", "2025-10-02T06:30", SleepQuality.GOOD))));
        assertEquals("PIGEON", value(chronotype, List.of(
                session("2025-10-01T23:30", "2025-10-02T09:30", SleepQuality.GOOD),
                session("2025-10-02T21:30", "2025-10-03T06:30", SleepQuality.GOOD))));
        assertEquals("PIGEON", value(chronotype, List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00", SleepQuality.GOOD))));
    }
}

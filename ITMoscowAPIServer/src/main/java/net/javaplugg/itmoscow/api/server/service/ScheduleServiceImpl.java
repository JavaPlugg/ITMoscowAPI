package net.javaplugg.itmoscow.api.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;
import net.javaplugg.itmoscow.api.dto.schedule.Lesson;
import net.javaplugg.itmoscow.api.dto.schedule.Replacement;
import net.javaplugg.itmoscow.api.dto.schedule.Schedule;
import net.javaplugg.itmoscow.api.server.exception.CannotApplyReplacementsException;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final HtmlFetchingService htmlFetchingService;
    private final String url;
    private final Clock clock;
    private final Cache<String, Map<Integer, Schedule>> scheduleCache;
    private final Cache<String, List<Replacement>> replacementCache;

    public ScheduleServiceImpl(ITMoscowAPIServerProperties properties, HtmlFetchingService htmlFetchingService) {
        this.htmlFetchingService = htmlFetchingService;
        this.url = properties.getItmoscowUrl();
        this.clock = Clock.offset(Clock.systemUTC(), Duration.ofHours(3));
        this.scheduleCache = Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build();
        this.replacementCache = Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public CompletableFuture<Schedule> getScheduleForDay(Building building, Group group, int weekday, boolean replacements) {
        int today = LocalDate.now(clock).getDayOfWeek().ordinal();
        if (weekday != today && replacements) {
            return CompletableFuture.failedFuture(new CannotApplyReplacementsException());
        }
        String cacheKey = building.toString() + group.toString();
        Map<Integer, Schedule> cache = scheduleCache.getIfPresent(cacheKey);
        if (cache != null) {
            Schedule schedule = cache.get(weekday);
            return replacements ? applyReplacements(building, group, schedule) : CompletableFuture.completedFuture(schedule);
        }
        String scheduleUrl = url + "/" + building.key() + "/" + UriUtils.encode(group.name(), StandardCharsets.UTF_8);
        return htmlFetchingService
                .fetchHtml(scheduleUrl)
                .thenApply(html -> parseAndCacheSchedule(building, group, weekday, html))
                .thenCompose(schedule -> replacements
                        ? applyReplacements(building, group, schedule)
                        : CompletableFuture.completedFuture(schedule)
                );
    }

    @Override
    public CompletableFuture<List<Replacement>> getReplacementsForToday(Building building, Group group) {
        String cacheKey = building.toString() + group.toString();
        List<Replacement> cache = replacementCache.getIfPresent(cacheKey);
        if (cache != null) {
            return CompletableFuture.completedFuture(cache);
        }
        String scheduleUrl = url + "/" + building.key() + "/" + group.name().replace(" ", "%20");
        return htmlFetchingService.fetchHtml(scheduleUrl).thenApply(html -> parseAndCacheReplacements(building, group, html));
    }

    private CompletableFuture<Schedule> applyReplacements(Building building, Group group, Schedule schedule) {
        return getReplacementsForToday(building, group).thenApply(replacements -> new Schedule(schedule.weekday(), schedule.lessons()
                .stream()
                .map(lesson -> replacements
                        .stream()
                        .filter(replacement -> lesson.number() == replacement.number())
                        .findFirst()
                        .map(replacement -> new Lesson(
                                lesson.number(),
                                lesson.time(),
                                replacement.subject(),
                                decideTeacher(lesson.teacher(), replacement.teacher()),
                                decideRoom(lesson.room(), replacement.room())
                        ))
                        .orElse(lesson)
                )
                .toList()
        ));
    }

    @SuppressWarnings("DataFlowIssue")
    private Schedule parseAndCacheSchedule(Building building, Group group, int weekday, String html) {
        try {
            Document document = Jsoup.parse(html);
            List<Schedule> schedules = document
                    .selectFirst("[class~=grid][class~=md:grid-cols-1][class~=lg:grid-cols-2][class~=gap-4][class~=sm:gap-6]")
                    .select("[class~=rounded-2xl][class~=border][class~=border-\\[#E7EEF6\\]][class~=bg-white][class~=p-4][class~=sm:p-6][class~=shadow-sm]")
                    .stream()
                    .map(element -> {
                        String weekdayName = element.selectFirst("[class~=font-bold][class~=text-\\[24px\\]][class~=sm:text-\\[28px\\]][class~=md:text-\\[32px\\]]").text();
                        List<Lesson> lessons = new ArrayList<>();
                        Elements lessonElements = element.select("[class~=flex][class~=flex-row][class~=gap-4][class~=p-3][class~=sm:p-4][class~=rounded-xl][class~=bg-\\[#F7FAFF\\]]");
                        lessonElements.forEach(lessonElement -> {
                            Element section1 = lessonElement.selectFirst("[class~=flex][class~=flex-col][class~=items-center][class~=shrink-0][class~=w-24][class~=text-center]");
                            Element section2 = lessonElement.selectFirst("[class~=flex][class~=flex-col][class~=flex-1][class~=justify-center][class~=items-start]");
                            String lessonNumber = section1.selectFirst("[class~=font-semibold][class~=text-sm][class~=sm:text-base]").text();
                            String lessonTime = section1.selectFirst("[class~=mt-1][class~=text-xs][class~=sm:text-sm][class~=text-gray-600]").text().replace(" ", " - ");
                            String lessonSubject = section2.selectFirst("[class~=font-semibold][class~=text-base][class~=sm:text-lg][class~=break-words]").text();
                            String lessonTeacher = section2.selectFirst("[class~=text-sm][class~=sm:text-base][class~=italic][class~=text-gray-600]").text();
                            String lessonRoom = section2.selectFirst("[class~=text-sm][class~=text-gray-500]").text();
                            Lesson lesson = new Lesson(
                                    extractNumber(lessonNumber),
                                    formatTime(lessonTime),
                                    lessonSubject,
                                    lessonTeacher,
                                    formatRoom(lessonRoom)
                            );
                            lessons.add(lesson);
                        });
                        return new Schedule(weekdayName, lessons);
                    })
                    .toList();
            Map<Integer, Schedule> scheduleMap = new HashMap<>();
            for (int i = 0; i < schedules.size(); i++) {
                scheduleMap.put(i, schedules.get(i));
            }
            String cacheKey = building.toString() + group.toString();
            scheduleCache.put(cacheKey, scheduleMap);
            return scheduleMap.get(weekday);
        } catch (NullPointerException e) {
            log.error("Failed to parse html. Website might have changed", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private List<Replacement> parseAndCacheReplacements(Building building, Group group, String html) {
        try {
            Document document = Jsoup.parse(html);
            Element replacementsElement = document.selectFirst("[class~=flex][class~=flex-col][class~=lg:flex-row][class~=lg:items-center][class~=gap-4][class~=p-4][class~=sm:p-6][class~=rounded-2xl][class~=border-2][class~=border-orange-200][class~=bg-orange-50]");
            String cacheKey = building.toString() + group.toString();
            if (replacementsElement == null) {
                replacementCache.put(cacheKey, List.of());
                return List.of();
            }
            List<Replacement> replacements = new ArrayList<>();
            Elements replacementsElements = replacementsElement
                    .selectFirst("[class~=flex-1][class~=grid][class~=grid-cols-1][class~=lg:grid-cols-2][class~=xl:grid-cols-3][class~=gap-3]")
                    .select("[class~=flex][class~=flex-row][class~=gap-3][class~=p-3][class~=rounded-xl][class~=bg-orange-100][class~=border][class~=border-orange-200]");
            replacementsElements.forEach(element -> {
                Element section1 = element.selectFirst("[class~=flex][class~=flex-col][class~=items-center][class~=shrink-0][class~=w-16][class~=text-center]");
                Element section2 = element.selectFirst("[class~=flex][class~=flex-col][class~=flex-1][class~=justify-center][class~=items-start][class~=min-w-0]");
                String replacementNumber = section1.selectFirst("[class~=font-semibold][class~=text-xs]").text();
                String replacementSubject = section2.selectFirst("[class~=font-semibold][class~=text-sm][class~=break-words][class~=text-orange-800]").text();
                String replacementTeacher = section2.selectFirst("[class~=text-xs][class~=italic][class~=text-orange-600]").text();
                String replacementRoom = section2.selectFirst("[class~=text-xs][class~=text-orange-500]").text();
                Replacement replacement = new Replacement(
                        extractNumber(replacementNumber),
                        replacementSubject,
                        extractTeacher(replacementTeacher),
                        replacementRoom
                );
                replacements.add(replacement);
            });
            replacementCache.put(cacheKey, replacements);
            return replacements;
        } catch (NullPointerException e) {
            log.error("Failed to parse html. Website might have changed", e);
            throw new RuntimeException(e);
        }
    }

    private int extractNumber(String number) {
        return Integer.parseInt(number.trim().split("\\s+")[0]);
    }

    private String formatTime(String time) {
        if (time.charAt(1) == ':') {
            time = '0' + time;
        }
        if (time.charAt(9) == ':') {
            time = time.substring(0, 8) + '0' + time.substring(8);
        }
        return time;
    }

    private String formatRoom(String room) {
        return room.isEmpty() ? room : room.substring(0, 1).toUpperCase() + room.substring(1);
    }

    private String extractTeacher(String string) {
        return string.substring("Кто заменяет: ".length());
    }

    private String decideTeacher(String lessonTeacher, String replacementTeacher) {
        return replacementTeacher == null || replacementTeacher.equals("замена кабинета") ? lessonTeacher : replacementTeacher;
    }

    private String decideRoom(String lessonRoom, String replacementRoom) {
        return replacementRoom == null ? lessonRoom : replacementRoom + " (замена)";
    }
}

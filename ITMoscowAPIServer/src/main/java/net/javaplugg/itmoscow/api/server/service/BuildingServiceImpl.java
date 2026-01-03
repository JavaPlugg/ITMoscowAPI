package net.javaplugg.itmoscow.api.server.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import net.javaplugg.itmoscow.api.server.util.SingleCache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BuildingServiceImpl implements BuildingService {

    private final HtmlFetchingService htmlFetchingService;
    private final String url;
    private final SingleCache<Map<String, String>> buildingCache;

    public BuildingServiceImpl(ITMoscowAPIServerProperties properties, HtmlFetchingService htmlFetchingService) {
        this.htmlFetchingService = htmlFetchingService;
        this.url = properties.getItmoscowUrl();
        this.buildingCache = new SingleCache<>(Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build()
        );
    }

    @Override
    public CompletableFuture<List<Building>> getAllBuildings() {
        Map<String, String> map = buildingCache.get();
        if (map != null) {
            return CompletableFuture.completedFuture(toBuildingList(map));
        }
        return htmlFetchingService.fetchHtml(url).thenApply(this::parseAndCache);
    }

    private List<Building> parseAndCache(String html) {
        try {
            Document document = Jsoup.parse(html);
            @SuppressWarnings("DataFlowIssue")
            Elements elements = document
                    .selectFirst("[class~=sticky][class~=top-0][class~=z-40][class~=bg-white][class~=border-b][class~=border-gray-100][class~=flex][class~=items-center][class~=justify-between][class~=px-4][class~=md:px-\\[70px\\]][class~=py-\\[20px\\]][class~=text-\\[14px\\]][class~=font-semibold][class~=w-full]")
                    .selectFirst("[class~=hidden][class~=lg:flex][class~=mx-auto][class~=flex-1][class~=justify-center][class~=gap-\\[22px\\]][class~=self-center][class~=px-\\[60px\\]][class~=py-\\[10px\\]]")
                    .selectFirst("[class~=group][class~=relative]")
                    .selectFirst("[class~=invisible][class~=absolute][class~=top-full][class~=left-0][class~=z-10][class~=mt-1][class~=w-56][class~=rounded-md][class~=border][class~=border-gray-200][class~=bg-white][class~=opacity-0][class~=shadow-lg][class~=transition-all][class~=duration-200][class~=group-hover:visible][class~=group-hover:opacity-100]")
                    .select("[class~=flex][class~=items-center][class~=gap-2][class~=px-4][class~=py-2][class~=text-sm][class~=hover:bg-gray-100]");
            Map<String, String> newCache = elements.stream().map(element -> {
                String key = element.attr("href").substring(1);
                String name = element.child(0).text();
                return new Building(name, key);
            }).collect(Collectors.toMap(
                    Building::key,
                    Building::name,
                    (a, b) -> b,
                    LinkedHashMap::new
            ));
            buildingCache.set(newCache);
            return toBuildingList(newCache);
        } catch (NullPointerException e) {
            log.error("Failed to parse html. Website might have changed", e);
            throw new RuntimeException(e);
        }
    }

    private List<Building> toBuildingList(Map<String, String> map) {
        return map
                .entrySet()
                .stream()
                .map(e -> new Building(e.getValue(), e.getKey()))
                .toList();
    }
}

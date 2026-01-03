package net.javaplugg.itmoscow.api.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.building.Building;
import net.javaplugg.itmoscow.api.dto.group.Group;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    private static final String QUERY_ASIDE = "[class~=lg:w-\\[300px\\]][class~=lg:shrink-0][class~=lg:flex][class~=lg:flex-col][class~=lg:min-h-0][class~=lg:py-8]";
    private static final String QUERY_NAV = "[class~=lg:h-full][class~=fixed][class~=inset-0][class~=z-40][class~=bg-black/40][class~=transition-opacity][class~=duration-200][class~=opacity-0][class~=pointer-events-none][class~=lg:static][class~=lg:bg-transparent][class~=lg:opacity-100][class~=lg:pointer-events-auto][class~=lg:translate-x-0][class~=lg:shadow-none][class~=lg:p-0][class~=lg:flex][class~=lg:flex-col]";
    private static final String QUERY_DIV = "[class~=absolute][class~=left-0][class~=top-0][class~=h-full][class~=w-\\[80vw\\]][class~=max-w-xs][class~=bg-white][class~=shadow-2xl][class~=p-6][class~=flex][class~=flex-col][class~=gap-4][class~=transform][class~=-translate-x-full][class~=transition-transform][class~=duration-200][class~=lg:static][class~=lg:transform-none][class~=lg:w-full][class~=lg:max-w-none][class~=lg:p-6][class~=lg:bg-white][class~=lg:flex][class~=lg:flex-col][class~=lg:h-full][class~=lg:rounded-2xl][class~=lg:border][class~=lg:border-\\[#E7EEF6\\]][class~=lg:shadow-sm][class~=lg:min-h-0]";
    private static final String QUERY_LIST = "[class~=flex][class~=flex-col][class~=gap-2][class~=flex-1][class~=overflow-y-auto][class~=min-h-0][class~=hide-scrollbar]";
    private static final String QUERY_ITEM = ".group-item";
    private static final String QUERY_NAME = "[class~=block][class~=px-4][class~=py-3][class~=rounded-lg][class~=font-medium][class~=transition-colors][class~=duration-150][class~=text-gray-700][class~=hover:bg-\\[#1357ff\\]/10][class~=hover:text-\\[#1357ff\\]]";

    private final HtmlFetchingService htmlFetchingService;
    private final String url;
    private final Cache<Building, List<Group>> groupCache;

    public GroupServiceImpl(ITMoscowAPIServerProperties properties, HtmlFetchingService htmlFetchingService) {
        this.htmlFetchingService = htmlFetchingService;
        this.url = properties.getItmoscowUrl();
        this.groupCache = Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public CompletableFuture<List<Group>> getGroupsByBuilding(Building building) {
        List<Group> groups = groupCache.getIfPresent(building);
        if (groups != null) {
            return CompletableFuture.completedFuture(groups);
        }
        String buildingUrl = url + "/" + building.key();
        return htmlFetchingService.fetchHtml(buildingUrl).thenApply(html -> parseAndCache(html, building));
    }

    private List<Group> parseAndCache(String html, Building building) {
        try {
            Document document = Jsoup.parse(html);
            @SuppressWarnings("DataFlowIssue")
            List<Group> groups = document
                    .selectFirst(QUERY_ASIDE)
                    .selectFirst(QUERY_NAV)
                    .selectFirst(QUERY_DIV)
                    .selectFirst(QUERY_LIST)
                    .select(QUERY_ITEM)
                    .stream()
                    .map(element -> element.selectFirst(QUERY_NAME))
                    .map(element -> element.text())
                    .map(Group::new)
                    .toList();
            groupCache.put(building, groups);
            return groups;
        } catch (NullPointerException e) {
            log.error("Failed to parse html. Website might have changed", e);
            throw new RuntimeException(e);
        }
    }
}

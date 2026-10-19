package nl.makan1869.capabilitymesh.mcpregistry.store;

import nl.makan1869.capabilitymesh.mcpregistry.model.OfficialMeta;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerDetail;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerList;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerListMetadata;
import nl.makan1869.capabilitymesh.mcpregistry.model.ServerResponse;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * In-memory, read-only store of server.json entries, queried per the generic registry
 * API (GET /v0.1/servers, /v0.1/servers/{name}/versions, /v0.1/servers/{name}/versions/{version}).
 *
 * All seeded entries are marked "active"; this registry supports no publish/delete
 * endpoint, so "deleted" status never actually occurs here today, but include_deleted
 * filtering is still implemented faithfully in case a future entry carries it.
 */
@Component
public class ServerRegistry {

    /** Fixed timestamp applied to all seeded entries, since they aren't really "published" at any real moment. */
    private static final Instant SEED_TIMESTAMP = Instant.parse("2026-01-01T00:00:00Z");

    private static final int DEFAULT_LIMIT = 30;

    private final List<ServerResponse> entries;

    public ServerRegistry(ServerSeedLoader seedLoader) {
        this.entries = buildEntries(seedLoader.loadAll());
    }

    private static List<ServerResponse> buildEntries(List<ServerDetail> seeded) {
        List<ServerDetail> sorted = seeded.stream()
                .sorted(Comparator.comparing(ServerDetail::name).thenComparing(ServerDetail::version))
                .toList();

        Map<String, String> latestVersionByName = sorted.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ServerDetail::name, ServerDetail::version, (a, b) -> b));

        return sorted.stream()
                .map(detail -> {
                    boolean isLatest = detail.version().equals(latestVersionByName.get(detail.name()));
                    OfficialMeta official = new OfficialMeta(
                            OfficialMeta.STATUS_ACTIVE, null, null, SEED_TIMESTAMP, SEED_TIMESTAMP, isLatest);
                    return ServerResponse.of(detail, official);
                })
                .toList();
    }

    public ServerList listServers(
            String cursor, Integer limit, String search, Instant updatedSince, String version, boolean includeDeleted) {
        boolean effectiveIncludeDeleted = includeDeleted || updatedSince != null;

        List<ServerResponse> filtered = entries.stream()
                .filter(e -> effectiveIncludeDeleted || !OfficialMeta.STATUS_DELETED.equals(e.official().status()))
                .filter(e -> search == null
                        || e.server().name().toLowerCase().contains(search.toLowerCase()))
                .filter(e -> updatedSince == null || !e.official().updatedAt().isBefore(updatedSince))
                .filter(e -> version == null || matchesVersionFilter(e, version))
                .toList();

        return paginate(filtered, cursor, limit);
    }

    private static boolean matchesVersionFilter(ServerResponse entry, String versionFilter) {
        if ("latest".equals(versionFilter)) {
            return Boolean.TRUE.equals(entry.official().isLatest());
        }
        return entry.server().version().equals(versionFilter);
    }

    private static ServerList paginate(List<ServerResponse> filtered, String cursor, Integer limit) {
        int startIndex = 0;
        if (cursor != null && !cursor.isBlank()) {
            for (int i = 0; i < filtered.size(); i++) {
                if (cursorKey(filtered.get(i)).equals(cursor)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        int pageSize = limit != null && limit > 0 ? limit : DEFAULT_LIMIT;
        int endIndex = Math.min(startIndex + pageSize, filtered.size());
        List<ServerResponse> page = startIndex < filtered.size()
                ? filtered.subList(startIndex, endIndex)
                : List.of();

        String nextCursor = endIndex < filtered.size() ? cursorKey(filtered.get(endIndex - 1)) : null;
        return new ServerList(page, new ServerListMetadata(nextCursor, page.size()));
    }

    private static String cursorKey(ServerResponse entry) {
        return entry.server().name() + ":" + entry.server().version();
    }

    public Optional<ServerList> listVersions(String serverName, boolean includeDeleted) {
        List<ServerResponse> all = entries.stream()
                .filter(e -> e.server().name().equals(serverName))
                .toList();
        if (all.isEmpty()) {
            return Optional.empty();
        }

        List<ServerResponse> visible = all.stream()
                .filter(e -> includeDeleted || !OfficialMeta.STATUS_DELETED.equals(e.official().status()))
                .toList();
        return Optional.of(new ServerList(visible, new ServerListMetadata(null, visible.size())));
    }

    public Optional<ServerResponse> getVersion(String serverName, String version, boolean includeDeleted) {
        return entries.stream()
                .filter(e -> e.server().name().equals(serverName))
                .filter(e -> "latest".equals(version)
                        ? Boolean.TRUE.equals(e.official().isLatest())
                        : e.server().version().equals(version))
                .filter(e -> includeDeleted || !OfficialMeta.STATUS_DELETED.equals(e.official().status()))
                .findFirst();
    }
}

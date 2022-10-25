package ru.practicum.ewmstat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT MAX(s.app) as app, s.uri as uri, COUNT (s.uri) as hits " +
            "FROM endpoint_hit as s " +
            "WHERE  s.time_stamp between ?1 AND ?2 " +
            "group by s.uri", nativeQuery = true)
    List<ViewStats> getByTime(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT MAX(s.app) as app, s.uri as uri, COUNT (s.uri) as hits " +
            "FROM (SELECT DISTINCT ip AS i FROM endpoint_hit) as s " +
            "WHERE  s.time_stamp between ?1 AND ?2 " +
            "group by s.uri", nativeQuery = true)
    List<ViewStats> getByTimeUniq(LocalDateTime start, LocalDateTime end);
}

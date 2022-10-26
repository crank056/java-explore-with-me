package ru.practicum.ewmstat;

import org.springframework.stereotype.Service;
import ru.practicum.ewmstat.model.EndpointHitDto;
import ru.practicum.ewmstat.model.StatsMapper;
import ru.practicum.ewmstat.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void save(EndpointHitDto endpointHitDto) {
        statsRepository.save(StatsMapper.toEntity(endpointHitDto));
    }

    public List<ViewStats> getStats(String startString, String endString, String[] uris, boolean uniq) {
        List<ViewStats> list;
        LocalDateTime start = LocalDateTime.parse(startString, formatter);
        LocalDateTime end = LocalDateTime.parse(endString, formatter);
        if (uniq) {
            list = statsRepository.getByTimeUniq(start, end);
        } else list = statsRepository.getByTime(start, end);
        if (uris.length != 0) {
            List<String> uriList = List.of(uris);
            list = list.stream().filter(viewStats -> uriList.contains(viewStats.getUri()))
                .collect(Collectors.toList());
        }
        return list;
    }
}

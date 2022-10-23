package ru.practicum.ewmstat;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstat.model.EndpointHitDto;
import ru.practicum.ewmstat.model.ViewStats;

import java.util.List;

@RestController
@RequestMapping

public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public void save(@RequestBody EndpointHitDto endpointHitDto) {
        statsService.save(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start, @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean uniq) {
        return statsService.getStats(start, end, uris, uniq);
    }
}

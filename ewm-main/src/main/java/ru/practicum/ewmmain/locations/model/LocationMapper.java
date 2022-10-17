package ru.practicum.ewmmain.locations.model;

public class LocationMapper {

    public static Location toEntity(LocationDto locationDto) {
        return new Location(
                locationDto.getId(),
                locationDto.getLat(),
                locationDto.getLon()
        );
    }

    public static LocationDto toDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getLat(),
                location.getLon()
        );
    }
}

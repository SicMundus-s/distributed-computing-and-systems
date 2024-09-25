package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto;

import java.util.List;

public record WordPageDto(long nextPage, List<WordDto> wordDtos) {
}

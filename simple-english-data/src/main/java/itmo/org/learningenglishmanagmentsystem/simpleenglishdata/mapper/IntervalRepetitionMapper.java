package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper;

import itmo.org.learningenglishmanagmentsystem.core.entity.IntervalRepetition;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.IntervalRepetitionDto;
import org.springframework.stereotype.Component;

@Component
public class IntervalRepetitionMapper {
    public IntervalRepetitionDto mapFromEntity(IntervalRepetition ir) {
        return new IntervalRepetitionDto(
                ir.getWordId().getTranslate(),
                ir.getCountCorrectTranslation()
        );
    }
}

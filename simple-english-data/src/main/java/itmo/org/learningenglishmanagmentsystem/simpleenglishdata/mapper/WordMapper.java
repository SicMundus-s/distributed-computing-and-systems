package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper;

import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import itmo.org.learningenglishmanagmentsystem.core.entity.Word;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordDto;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {
    public WordDto mapFromEntity(Word w) {
        WordDto wordDto = new WordDto();
        wordDto.setWord(w.getWord());
        wordDto.setTranslation(w.getTranslate());
        return wordDto;
    }

    public PopularWordDto mapFromEntityPopularWord(PopularWordDto pw) {
        PopularWordDto popularWordDto = new PopularWordDto();
        popularWordDto.setWord(pw.getWord());
        popularWordDto.setTranslate(pw.getTranslate());
        return popularWordDto;
    }
}

package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;


import itmo.org.learningenglishmanagmentsystem.core.entity.IntervalRepetition;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.security.exception.ValidationException;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.IntervalRepetitionDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.IntervalRepetitionMapper;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.IntervalRepetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntervalRepetitionService {

    private final IntervalRepetitionRepository intervalRepetitionRepository;
    private final UserService userService;
    private final WordService wordService;
    private final IntervalRepetitionMapper intervalRepetitionMapper;
    public Long addWordToListIntervalRepetition(Long wordId, Authentication auth) {
        var user = userService.findByLogin(userService.getLogin(auth));
        var word = wordService.findOne(wordId);
        IntervalRepetition intervalRepetition = new IntervalRepetition(user, word);
        return intervalRepetitionRepository.save(intervalRepetition).getUserId().getId();
    }

    public Optional<IntervalRepetitionDto> findWordForListIntervalRepetition(Authentication authentication) {
        User user = userService.findByLogin(userService.getLogin(authentication));
        return intervalRepetitionRepository
                .findWordForListIntervalRepetition(user.getId()).map(intervalRepetitionMapper::mapFromEntity);
    }

    public Optional<IntervalRepetition>  findIntervalRepetitionByUserIdAndWordId(Long userId, Long wordId) {
        return intervalRepetitionRepository.findIntervalRepetitionByUserIdAndWordId(userId, wordId);
    }

    public void checkingCorrectWordTranslation(Long userId, Long wordId, String wordTranslate) {
        Optional<IntervalRepetition> optionalIntervalRep = findIntervalRepetitionByUserIdAndWordId(userId, wordId);
        if (optionalIntervalRep.isPresent()) {
            IntervalRepetition wordIntervalRepetition = optionalIntervalRep.get();
            var wordTranslations = wordIntervalRepetition.getWordId().getTranslate().split(", ");
            if (Arrays.asList(wordTranslations).contains(wordTranslate)) {
                wordIntervalRepetition.setLastTranslation(new Date());
                wordIntervalRepetition.setCountCorrectTranslation(wordIntervalRepetition.getCountCorrectTranslation() + 1);
                if (wordIntervalRepetition.getCountCorrectTranslation() == 3) {
                    intervalRepetitionRepository.delete(wordIntervalRepetition);
                } else {
                    intervalRepetitionRepository.save(wordIntervalRepetition);
                }
            }
        } else {
            throw new ValidationException("Parametrs incorrect");
        }
    }
}


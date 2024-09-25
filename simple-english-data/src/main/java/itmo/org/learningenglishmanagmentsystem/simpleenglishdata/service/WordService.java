package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;


import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import itmo.org.learningenglishmanagmentsystem.core.entity.Word;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.exception.WordNotFoundException;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.WordMapper;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private static final Sort PAGING_ORDER = Sort.by(Sort.Order.by("w.word"));

    private final WordRepository wordRepository;
    private final UserService userService;
    private final WordMapper wordMapper;
    private final UserRepository userRepository;

    public List<WordDto> searchWords(String search) {
        return wordRepository.findByWordStartingWith(search).stream()
                .map(wordMapper::mapFromEntity).collect(Collectors.toList());
    }

    public Word findOne(Long wordId) {
        return wordRepository.findById(wordId)
                             .orElseThrow(() -> new WordNotFoundException("Word not found"));
    }

    public List<PopularWordDto> getPopularWords() {
        return wordRepository.getPopularWords().stream().map(wordMapper::mapFromEntityPopularWord).collect(Collectors.toList());
    }

    public WordDto addWordToDictionary(Long wordId, Authentication auth) {
        var user = userService.findByLogin(userService.getLogin(auth));
        Optional<Word> optionalWord = wordRepository.findById(wordId);
        if (user != null && optionalWord.isPresent()) {
            Word word = optionalWord.get();
            user.getWords().add(word);
            userRepository.save(user);
            return wordMapper.mapFromEntity(word);
        } else {
            throw new IllegalArgumentException("User or word not found");
        }
    }

    public Set<WordDto> getCategoryWords(String name) {
        return wordRepository.findAllByCategories(name).stream()
                .map(wordMapper::mapFromEntity)
                .collect(Collectors.toSet());
    }

    public List<WordDto> findAllByUserIdFromDictionary(Long userId, Integer page, Integer count) {
        Pageable pageable = PageRequest.of(page, count, PAGING_ORDER);
        return wordRepository.findAllByUserIdFromDictionary(userId, pageable)
                .stream()
                .map(wordMapper::mapFromEntity)
                .collect(Collectors.toList());
    }
}

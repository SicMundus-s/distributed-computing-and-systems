package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;

import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.core.entity.Word;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.WordMapper;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {

    private static final Sort PAGING_ORDER = Sort.by(Sort.Order.by("w.word"));
    @Mock
    private WordRepository wordRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WordMapper wordMapper;

    @InjectMocks
    private WordService wordService;

    @Test
    void searchWords_ValidSearch_ReturnsWordDtoList() {
        // Given
        String search = "test";
        List<Word> mockWordList = Arrays.asList(new Word(), new Word());
        given(wordRepository.findByWordStartingWith(search)).willReturn(mockWordList);
        given(wordMapper.mapFromEntity(any(Word.class))).willReturn(new WordDto());

        // When
        List<WordDto> result = wordService.searchWords(search);

        // Then
        assertNotNull(result);
        assertEquals(mockWordList.size(), result.size());
        then(wordRepository).should(times(1)).findByWordStartingWith(search);
        then(wordMapper).should(times(mockWordList.size())).mapFromEntity(any(Word.class));
    }

    @Test
    void findOne_ExistingWordId_ReturnsWord() {
        // Given
        long wordId = 1L;
        Word mockWord = new Word();
        given(wordRepository.findById(wordId)).willReturn(Optional.of(mockWord));

        // When
        Word result = wordService.findOne(wordId);

        // Then
        assertNotNull(result);
        then(wordRepository).should(times(1)).findById(wordId);
    }

    @Test
    void getPopularWords_ValidData_ReturnsPopularWordDtoList() {
        // Given
        List<PopularWordDto> mockPopularWordList = Arrays.asList(new PopularWordDto(), new PopularWordDto());
        given(wordRepository.getPopularWords()).willReturn(mockPopularWordList);
        given(wordMapper.mapFromEntityPopularWord(any(PopularWordDto.class))).willReturn(new PopularWordDto());

        // When
        List<PopularWordDto> result = wordService.getPopularWords();

        // Then
        assertNotNull(result);
        assertEquals(mockPopularWordList.size(), result.size());
        then(wordRepository).should(times(1)).getPopularWords();
        then(wordMapper).should(times(mockPopularWordList.size())).mapFromEntityPopularWord(any(PopularWordDto.class));
    }

    @Test
    void addWordToDictionary_ValidWordIdAndAuth_ReturnsWordDto() {
        // Given
        Long wordId = 1L;
        String login = "test";
        Authentication mockAuth = mock(Authentication.class);
        User mockUser = new User();
        Word mockWord = new Word();
        HashSet<Word> givenHashSetWold = new HashSet<>();
        givenHashSetWold.add(mockWord);
        mockUser.setWords(givenHashSetWold);
        given(userService.getLogin(mockAuth)).willReturn(login);
        given(userService.findByLogin(anyString())).willReturn(mockUser);
        given(wordRepository.findById(wordId)).willReturn(Optional.of(mockWord));
        given(userRepository.save(any(User.class))).willReturn(mockUser);
        given(wordMapper.mapFromEntity(any(Word.class))).willReturn(new WordDto());

        // When
        WordDto result = wordService.addWordToDictionary(wordId, mockAuth);

        // Then
        assertNotNull(result);
        then(userService).should(times(1)).findByLogin(anyString());
        then(wordRepository).should(times(1)).findById(wordId);
        then(userRepository).should(times(1)).save(any(User.class));
        then(wordMapper).should(times(1)).mapFromEntity(any(Word.class));
    }

    @Test
    void getCategoryWords_ValidName_ReturnsWordDtoSet() {
        // Given
        String categoryName = "category";
        Set<Word> mockWordSet = new HashSet<>(Arrays.asList(new Word(), new Word()));
        given(wordRepository.findAllByCategories(categoryName)).willReturn(mockWordSet);
        given(wordMapper.mapFromEntity(any(Word.class))).willReturn(new WordDto());

        // When
        Set<WordDto> result = wordService.getCategoryWords(categoryName);

        // Then
        assertNotNull(result);
        assertEquals(mockWordSet.size(), result.size());
        then(wordRepository).should(times(1)).findAllByCategories(categoryName);
        then(wordMapper).should(times(mockWordSet.size())).mapFromEntity(any(Word.class));
    }

    @Test
    void findAllByUserIdFromDictionary_ValidUserIdAndPagination_ReturnsWordDtoList() {
        // Given
        Long userId = 1L;
        int page = 0;
        int count = 10;
        List<Word> mockWordList = Arrays.asList(new Word(), new Word());
        Page<Word> mockPage = new PageImpl<>(mockWordList);
        given(wordRepository.findAllByUserIdFromDictionary(userId, PageRequest.of(page, count, PAGING_ORDER)))
                .willReturn(mockPage);
        given(wordMapper.mapFromEntity(any(Word.class))).willReturn(new WordDto());

        // When
        List<WordDto> result = wordService.findAllByUserIdFromDictionary(userId, page, count);

        // Then
        assertNotNull(result);
        assertEquals(mockWordList.size(), result.size());
        then(wordRepository).should(times(1))
                .findAllByUserIdFromDictionary(userId, PageRequest.of(page, count, PAGING_ORDER));
        then(wordMapper).should(times(mockWordList.size())).mapFromEntity(any(Word.class));
    }
}
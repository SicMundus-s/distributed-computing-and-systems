package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;

import itmo.org.learningenglishmanagmentsystem.core.entity.IntervalRepetition;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.core.entity.Word;
import itmo.org.learningenglishmanagmentsystem.security.exception.ValidationException;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.IntervalRepetitionMapper;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.IntervalRepetitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntervalRepetitionServiceTest {

    @Mock
    private IntervalRepetitionRepository intervalRepetitionRepository;

    @Mock
    private UserService userService;

    @Mock
    private WordService wordService;

    @Mock
    private IntervalRepetitionMapper intervalRepetitionMapper;

    @InjectMocks
    private IntervalRepetitionService intervalRepetitionService;

    @Test
    public void testAddWordToListIntervalRepetition() {
        // Given
        Long wordId = 1L;
        String login = "testUser";
        Authentication authentication = mock(Authentication.class);
        User user = new User(); // создаем тестового пользователя
        user.setId(1L);
        Word word = new Word(); // создаем тестовое слово
        word.setId(wordId);

        given(userService.getLogin(authentication)).willReturn(login);
        given(userService.findByLogin(login)).willReturn(user);
        given(wordService.findOne(wordId)).willReturn(word);
        given(intervalRepetitionRepository.save(any(IntervalRepetition.class)))
                .willAnswer(invocation -> {
                    IntervalRepetition intervalRepetition = invocation.getArgument(0);
                    return intervalRepetition;
                });

        // When
        Long result = intervalRepetitionService.addWordToListIntervalRepetition(wordId, authentication);

        // Then
        assertThat(result).isEqualTo(user.getId());
        verify(intervalRepetitionRepository, times(1)).save(any(IntervalRepetition.class));
    }

    @Test
    public void testCheckingCorrectWordTranslation() {
        // Given
        User user = new User();
        user.setId(1L);

        Word word = new Word();
        word.setId(1L);
        word.setTranslate("translation1, translation2");

        IntervalRepetition intervalRepetition = new IntervalRepetition(user, word);
        intervalRepetition.setCountCorrectTranslation(2);

        when(intervalRepetitionRepository.findIntervalRepetitionByUserIdAndWordId(anyLong(), anyLong()))
                .thenReturn(Optional.of(intervalRepetition));

        intervalRepetitionService.checkingCorrectWordTranslation(1L, 1L, "translation1");

        assertThat(intervalRepetition.getLastTranslation()).isNotNull();
        assertThat(intervalRepetition.getCountCorrectTranslation()).isEqualTo(3);
    }

    @Test
    public void testCheckingCorrectWordTranslation_InvalidParameters() {
        // Given
        Long userId = 1L;
        Long wordId = 1L;
        String wordTranslate = "translation";

        given(intervalRepetitionRepository.findIntervalRepetitionByUserIdAndWordId(userId, wordId))
                .willReturn(Optional.empty());

        // When/Then
        assertThrows(ValidationException.class,
                () -> intervalRepetitionService.checkingCorrectWordTranslation(userId, wordId, wordTranslate));
    }
}
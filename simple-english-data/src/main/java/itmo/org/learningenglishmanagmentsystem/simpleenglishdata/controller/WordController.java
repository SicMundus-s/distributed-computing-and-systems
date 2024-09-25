package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.org.learningenglishmanagmentsystem.core.dto.PopularWordDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.IntervalRepetitionDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.PopularWordsDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordsCategoryDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordsDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.exception.WordNotFoundException;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.IntervalRepetitionService;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.WordService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("simple-english/user/")
@RequiredArgsConstructor
@Tag(name = "WordController", description = "Контроллер для работы со словами и категориями")
public class WordController {

    private final WordService wordService;
    private final IntervalRepetitionService intervalRepetitionService;

    @Operation(summary = "Получение 1000 популярных слов", description = "Предоставляет список популярных слов")
    @ApiResponse(responseCode = "200", description = "Список популярных слов предоставлен", content = @Content(schema = @Schema(implementation = PopularWordsDto.class)))
    @GetMapping("1000-popular-words")
    public ResponseEntity<EntityModel<PopularWordsDto>> popularWords() {
        List<PopularWordDto> popularWords = wordService.getPopularWords();
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                .popularWords()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(new PopularWordsDto(popularWords), selfLink));
    }

    @Operation(summary = "Поиск слов", description = "Позволяет найти слова по заданному запросу")
    @ApiResponse(responseCode = "200", description = "Результаты поиска предоставлены", content = @Content(schema = @Schema(implementation = WordsDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    @GetMapping("search-word")
    public ResponseEntity<EntityModel<WordsDto>> search(@RequestParam(name = "search") String search) {
        try {
            List<WordDto> wordDtos = wordService.searchWords(search);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                    .search(search)).withSelfRel();
            return ResponseEntity.ok(EntityModel.of(new WordsDto(wordDtos), selfLink));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Добавление слова в словарь пользователя", description = "Добавляет слово в словарь пользователя")
    @ApiResponse(responseCode = "200", description = "Слово добавлено в словарь")
    @ApiResponse(responseCode = "400", description = "Неверный запрос или слово не найдено")
    @PostMapping("add-word-dictionary")
    public ResponseEntity<?> addWordToDictionary(@RequestParam(name = "wordId") Long wordId,
                                                 Authentication authentication) {
        try {
            WordDto wordDto = wordService.addWordToDictionary(wordId, authentication);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                    .addWordToDictionary(wordId, authentication)).withSelfRel();
            return ResponseEntity.ok(EntityModel.of(wordDto, selfLink));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ValidationException(e.getMessage()));
        }
    }

    @Operation(summary = "Добавление слова в список интервального повторения", description = "Добавляет слово в список интервального повторения пользователя")
    @ApiResponse(responseCode = "200", description = "Слово добавлено в список")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос или слово не найдено")
    @PostMapping("add-word-interval-repetition")
    public ResponseEntity<?> addWordToListIntervalRepetition(@RequestParam(name = "wordIdForIntervalRepetition") Long wordId,
                                                  Authentication authentication) {
        try {
            Long saveUserId = intervalRepetitionService.addWordToListIntervalRepetition(wordId, authentication);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                    .addWordToListIntervalRepetition(wordId, authentication)).withSelfRel();
            return ResponseEntity.ok(EntityModel.of(saveUserId, selfLink));
        } catch (WordNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Показать слова в списке интервального повторения", description = "Отображает слова в списке интервального повторения пользователя")
    @ApiResponse(responseCode = "200", description = "Список слов предоставлен", content = @Content(schema = @Schema(implementation = IntervalRepetitionDto.class)))
    @ApiResponse(responseCode = "404", description = "Список слов не найден")
    @GetMapping("interval-repetition")
    public ResponseEntity<EntityModel<IntervalRepetitionDto>> showIntervalRepetition(Authentication authentication) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                .showIntervalRepetition(authentication)).withSelfRel();
        return intervalRepetitionService.findWordForListIntervalRepetition(authentication)
                .map(ir -> ResponseEntity.ok(EntityModel.of(ir, selfLink)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Operation(summary = "Проверка корректности перевода слова", description = "Проверяет корректность перевода слова пользователем")
    @ApiResponse(responseCode = "200", description = "Проверка успешна")
    @ApiResponse(responseCode = "404", description = "Слово или пользователь не найдены")
    @PostMapping("check-interval-repetition")
    public ResponseEntity<?> checkCorrectWordTranslation(@RequestParam(name = "wordTranslate") String wordTranslate,
                                                    @RequestParam(name = "wordId") Long wordId,
                                                    @RequestParam(name = "userId") Long userId) {
        try {
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                    .checkCorrectWordTranslation(wordTranslate, wordId, userId)).withSelfRel();
            intervalRepetitionService.checkingCorrectWordTranslation(userId, wordId, wordTranslate);
            return ResponseEntity.ok().body(selfLink);
        }catch (ValidationException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получение категории слов - Фрукты", description = "Предоставляет список слов в категории Фрукты")
    @ApiResponse(responseCode = "200", description = "Список слов в категории Фрукты предоставлен", content = @Content(schema = @Schema(implementation = WordsCategoryDto.class)))
    @GetMapping("category/fruits")
    public ResponseEntity<EntityModel<WordsCategoryDto>> categoryFood() {
        Set<WordDto> wordDtos = wordService.getCategoryWords("Fruits");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                .categoryFood()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(new WordsCategoryDto(wordDtos), selfLink));
    }

    @Operation(summary = "Получение категории слов - Овощи", description = "Предоставляет список слов в категории Овощи")
    @ApiResponse(responseCode = "200", description = "Список слов в категории Овощи предоставлен", content = @Content(schema = @Schema(implementation = WordsCategoryDto.class)))
    @GetMapping("category/vegetables")
    public ResponseEntity<EntityModel<WordsCategoryDto>>  categoryVegetables() {
        Set<WordDto> wordDtos = wordService.getCategoryWords("Vegetables");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                .categoryVegetables()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(new WordsCategoryDto(wordDtos), selfLink));
    }

    @Operation(summary = "Получение категории слов - Животные", description = "Предоставляет список слов в категории Животные")
    @ApiResponse(responseCode = "200", description = "Список слов в категории Животные предоставлен", content = @Content(schema = @Schema(implementation = WordsCategoryDto.class)))
    @GetMapping("category/animals")
    public ResponseEntity<EntityModel<WordsCategoryDto>>  categoryAnimals() {
        Set<WordDto> wordDtos = wordService.getCategoryWords("Animals");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WordController.class)
                .categoryAnimals()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(new WordsCategoryDto(wordDtos), selfLink));
    }
}

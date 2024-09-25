package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.SaveResponse;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.UserDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.WordPageDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.UserService;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("simple-english/user/")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "Контроллер для управления профилями пользователей")
public class UserController {

    private final UserService userService;
    private final WordService wordService;

    @Operation(summary = "Получение словаря пользователя", description = "Предоставляет словарь пользователя")
    @ApiResponse(responseCode = "200", description = "Словарь предоставлен", content = @Content(schema = @Schema(implementation = WordPageDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    @GetMapping("dictionary")
    public ResponseEntity<EntityModel<WordPageDto>> dictionary(Authentication authentication,
                                                  @RequestParam(name = "page") String page,
                                                  @RequestParam(name = "count") String count) {
        User byLogin = userService.findByLogin(userService.getLogin(authentication));
        try {
            int pageNumber = Integer.parseInt(page);
            List<WordDto> dictionary = wordService.findAllByUserIdFromDictionary(
                    byLogin.getId(),
                    Integer.parseInt(page),
                    Integer.parseInt(count));

            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                    .dictionary(authentication, page, count)).withSelfRel();

            WordPageDto wordPageDto = new WordPageDto(pageNumber + 1, dictionary);
            EntityModel<WordPageDto> wordPageDtoEntityModel = EntityModel.of(wordPageDto, selfLink);

            return ResponseEntity.ok(wordPageDtoEntityModel);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Обновление профиля пользователя", description = "Обновляет профиль пользователя")
    @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен", content = @Content(schema = @Schema(implementation = SaveResponse.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя или пользователь не существует")
    @PostMapping("{id}")
    public ResponseEntity<?> updateProfile(@RequestBody UserDto userDto,
                                           @PathVariable("id") Long id) {
        Long saveId = userService.update(userDto, id);

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .updateProfile(userDto, id)).withSelfRel();

        if (saveId == null) {
            return ResponseEntity.badRequest().body("User is not exist");
        } else {
            EntityModel<SaveResponse> entityModel = EntityModel.of(new SaveResponse(saveId), selfLink);
            return ResponseEntity.ok(entityModel);
        }
    }
}

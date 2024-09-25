package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.SaveResponse;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.UserDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.AdminService;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("simple-english/admin/")
@RequiredArgsConstructor
@Tag(name = "AdminController", description = "Контроллер для административных функций")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по логину")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @DeleteMapping("delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam(name = "loginForDelete") String userLogin) {
        Optional<User> user = userService.OptionalFindByLogin(userLogin);
        if (user.isPresent()) {
            adminService.deleteUser(userLogin);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Показать пользователя", description = "Показывает данные пользователя по идентификатору")
    @ApiResponse(responseCode = "200", description = "Информация о пользователе предоставлена", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @GetMapping("show-user/{id}")
    public ResponseEntity<?> showUser(@PathVariable("id") Long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class)
                    .showUser(id)).withSelfRel();
            EntityModel<User> userEntityModel = EntityModel.of(user, selfLink);
            return ResponseEntity.ok(userEntityModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Обновление профиля пользователя", description = "Обновляет данные профиля пользователя")
    @ApiResponse(responseCode = "200", description = "Профиль пользователя обновлен")
    @ApiResponse(responseCode = "404", description = "Пользователь для обновления не найден")
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserDto userDto, @PathVariable("id") Long id) {
        Long saveId = userService.update(userDto, id);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class)
                .updateUserProfile(userDto, id)).withSelfRel();

        if (saveId == null) {
            return ResponseEntity.notFound().build();
        } else {
            EntityModel<SaveResponse> entityModel = EntityModel.of(new SaveResponse(saveId), selfLink);
            return ResponseEntity.ok(entityModel);
        }
    }

    @Operation(summary = "Назначить пользователя администратором", description = "Назначает пользователя администратором системы")
    @ApiResponse(responseCode = "200", description = "Пользователь назначен администратором")
    @ApiResponse(responseCode = "404", description = "Пользователь для назначения не найден")
    @PostMapping("set-admin")
    public ResponseEntity<String> setAdmin(@RequestParam(name = "userLogin") String userLogin) {
        Optional<User> user = userService.OptionalFindByLogin(userLogin);
        if (user.isPresent()) {
            adminService.setAdmin(user.get());
            return ResponseEntity.ok("User set as admin successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

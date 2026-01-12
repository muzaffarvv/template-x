package uz.vv.templatex.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uz.vv.templatex.dto.UserCreateDTO
import uz.vv.templatex.dto.UserResponseDTO
import uz.vv.templatex.dto.UserUpdateDTO
import uz.vv.templatex.service.UserService
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: UserCreateDTO): UserResponseDTO = userService.create(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody dto: UserUpdateDTO): UserResponseDTO = userService.update(id, dto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): UserResponseDTO = userService.getById(id)

    @GetMapping
    fun getAll(): List<UserResponseDTO> = userService.getAll()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) = userService.delete(id)
}

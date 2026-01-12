package uz.vv.templatex.dto

import java.util.UUID

data class UserResponseDTO(
    val id: UUID?,
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val organizationId: UUID?,
    val roleIds: List<UUID>
)

data class UserCreateDTO(
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val password: String,
    val organizationId: UUID,
    val roleIds: List<UUID>
)

data class UserUpdateDTO(
    val firstName: String?,
    val lastName: String?,
    val username: String?,
    val organizationId: UUID?,
    val roleIds: List<UUID>?
)
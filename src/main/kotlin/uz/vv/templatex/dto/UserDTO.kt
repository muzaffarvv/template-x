package uz.vv.templatex.dto

import uz.vv.templatex.base.BaseDTO
import java.util.UUID

data class UserResponseDTO(
    override val id: UUID?,
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val organizationId: UUID?,
    val roleIds: List<UUID>
) : BaseDTO

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
package uz.vv.templatex.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.vv.templatex.dto.UserCreateDTO
import uz.vv.templatex.dto.UserResponseDTO
import uz.vv.templatex.dto.UserUpdateDTO
import uz.vv.templatex.entity.User
import uz.vv.templatex.repository.OrganizationRepository
import uz.vv.templatex.repository.RoleRepository
import uz.vv.templatex.repository.UserRepository
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun create(dto: UserCreateDTO): UserResponseDTO {
        if (userRepository.existsByUsername(dto.username)) {
            throw RuntimeException("Username already exists: ${dto.username}")
        }

        val organization = organizationRepository.findById(dto.organizationId)
            .orElseThrow { RuntimeException("Organization not found: ${dto.organizationId}") }

        val roles = roleRepository.findAllByIdIn(dto.roleIds)

        val user = User(
            firstName = dto.firstName,
            lastName = dto.lastName,
            username = dto.username,
            password = passwordEncoder.encode(dto.password),
            organization = organization,
            roles = roles
        )

        return userRepository.save(user).toResponseDTO()
    }

    @Transactional
    fun update(id: UUID, dto: UserUpdateDTO): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("User not found: $id") }

        dto.firstName?.let { user.firstName = it }
        dto.lastName?.let { user.lastName = it }
        dto.username?.let {
            if (it != user.username && userRepository.existsByUsername(it)) {
                throw RuntimeException("Username already exists: $it")
            }
            user.username = it
        }
        dto.organizationId?.let {
            user.organization = organizationRepository.findById(it)
                .orElseThrow { RuntimeException("Organization not found: $it") }
        }
        dto.roleIds?.let {
            user.roles = roleRepository.findAllByIdIn(it)
        }

        return userRepository.save(user).toResponseDTO()
    }

    fun getById(id: UUID): UserResponseDTO {
        return userRepository.findById(id)
            .map { it.toResponseDTO() }
            .orElseThrow { RuntimeException("User not found: $id") }
    }

    fun getAll(): List<UserResponseDTO> {
        return userRepository.findAll().map { it.toResponseDTO() }
    }

    @Transactional
    fun delete(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw RuntimeException("User not found: $id")
        }
        userRepository.deleteById(id)
    }

    private fun User.toResponseDTO(): UserResponseDTO {
        return UserResponseDTO(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            username = this.username,
            organizationId = this.organization.id,
            roleIds = this.roles.mapNotNull { it.id }
        )
    }
}

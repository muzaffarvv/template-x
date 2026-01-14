package uz.vv.templatex.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.vv.templatex.base.BaseServiceImpl
import uz.vv.templatex.dto.UserCreateDTO
import uz.vv.templatex.dto.UserResponseDTO
import uz.vv.templatex.dto.UserUpdateDTO
import uz.vv.templatex.entity.User
import uz.vv.templatex.exception.OrganizationNotFoundException
import uz.vv.templatex.exception.RoleNotFoundException
import uz.vv.templatex.exception.UserNotFoundException
import uz.vv.templatex.exception.UsernameAlreadyExistsException
import uz.vv.templatex.repo.OrganizationRepo
import uz.vv.templatex.repo.RoleRepo
import uz.vv.templatex.repo.UserRepo
import uz.vv.templatex.base.BaseMapper
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepo,
    private val organizationRepository: OrganizationRepo,
    private val roleRepository: RoleRepo,
    private val passwordEncoder: PasswordEncoder
) : BaseServiceImpl<User, UserCreateDTO, UserUpdateDTO, UserResponseDTO, UserService.UserMapperStub, UserRepo>(
    userRepository, UserMapperStub()
) {

    class UserMapperStub : BaseMapper<User, UserResponseDTO> {
        override fun toDTO(entity: User): UserResponseDTO {
            return UserResponseDTO(
                id = entity.id,
                firstName = entity.firstName,
                lastName = entity.lastName,
                username = entity.username,
                organizationId = entity.organization.id,
                roleIds = entity.roles.mapNotNull { it.id }
            )
        }
    }

    override fun convertCreateDtoToEntity(dto: UserCreateDTO): User {
        throw UnsupportedOperationException("User creation logic is complex, use custom create method")
    }

    override fun updateEntityFromDto(entity: User, dto: UserUpdateDTO) {
        throw UnsupportedOperationException("User update logic is complex, use custom update method")
    }

    @Transactional
    override fun create(dto: UserCreateDTO): UserResponseDTO {
        if (userRepository.findByUsernameAndDeletedFalse(dto.username) != null) {
            throw UsernameAlreadyExistsException("Username already exists: ${dto.username}")
        }

        val organization = organizationRepository.findByIdAndDeletedFalse(dto.organizationId)
            ?: throw OrganizationNotFoundException("Organization not found: ${dto.organizationId}")

        val roles = if (dto.roleIds.isEmpty()) {
            roleRepository.findByCodeAndDeletedFalse("USER")?.let { mutableSetOf(it) } ?: throw RoleNotFoundException("Default role 'USER' not found")
        } else {
            val foundRoles = roleRepository.findAllByIdInAndDeletedFalse(dto.roleIds)
            if (foundRoles.size != dto.roleIds.size) {
                throw RoleNotFoundException("One or more roles not found")
            }
            foundRoles
        }

        val nextCodeNumber = (userRepository.findMaxCodeNumber() ?: 0) + 1
        val code = "EMP${nextCodeNumber.toString().padStart(3, '0')}"

        val user = User(
            firstName = dto.firstName,
            lastName = dto.lastName,
            username = dto.username,
            password = passwordEncoder.encode(dto.password),
            organization = organization,
            roles = roles,
            code = code
        )

        return userRepository.saveAndRefresh(user).toResponseDTO()
    }

    @Transactional
    override fun update(id: UUID, dto: UserUpdateDTO): UserResponseDTO {
        val user = userRepository.findByIdAndDeletedFalse(id)
            ?: throw UserNotFoundException("User not found: $id")

        dto.firstName?.let { user.firstName = it }
        dto.lastName?.let { user.lastName = it }
        dto.username?.let {
            if (it != user.username && userRepository.findByUsernameAndDeletedFalse(it) != null) {
                throw UsernameAlreadyExistsException("Username already exists: $it")
            }
            user.username = it
        }
        dto.organizationId?.let {
            user.organization = organizationRepository.findByIdAndDeletedFalse(it)
                ?: throw OrganizationNotFoundException("Organization not found: $it")
        }
        dto.roleIds?.let {
            val roles = roleRepository.findAllByIdInAndDeletedFalse(it)
            if (roles.isEmpty()) {
                throw RoleNotFoundException("At least one role must be assigned to the user")
            }
            user.roles = roles
        }

        return userRepository.save(user).toResponseDTO()
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): UserResponseDTO {
        return userRepository.findByIdAndDeletedFalse(id)?.toResponseDTO()
            ?: throw UserNotFoundException("User not found: $id")
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<UserResponseDTO> {
        return userRepository.findAllNotDeleted().map { it.toResponseDTO() }
    }

    @Transactional
    override fun delete(id: UUID) {
        userRepository.trash(id) ?: throw UserNotFoundException("User not found: $id")
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

package uz.vv.templatex.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uz.vv.templatex.entity.Permission
import uz.vv.templatex.entity.Role
import uz.vv.templatex.entity.User
import uz.vv.templatex.entity.Organization
import uz.vv.templatex.enum.PermissionType
import uz.vv.templatex.repo.OrganizationRepo
import uz.vv.templatex.repo.PermissionRepo
import uz.vv.templatex.repo.RoleRepo
import uz.vv.templatex.repo.UserRepo
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataInitializer(
    private val roleRepository: RoleRepo,
    private val permissionRepository: PermissionRepo,
    private val userRepository: UserRepo,
    private val organizationRepository: OrganizationRepo,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    fun initData() = CommandLineRunner {
        // 1. Initialize Permissions
        val permissions = PermissionType.entries.map { type ->
            permissionRepository.findByCodeAndDeletedFalse(type.code) ?: permissionRepository.save(
                Permission(code = type.code, name = type.description)
            )
        }

        // 2. Initialize Roles
        val adminRole = roleRepository.findByCodeAndDeletedFalse("ADMIN") ?: roleRepository.save(
            Role(code = "ADMIN", name = "Administrator")
        )
        
        val userRole = roleRepository.findByCodeAndDeletedFalse("USER") ?: roleRepository.save(
            Role(code = "USER", name = "Standard User")
        )

        // 3. Assign Permissions to Roles
        // Admin gets all permissions
        adminRole.permissions = permissions.toMutableSet()
        roleRepository.save(adminRole)

        // User gets only read permissions
        val userPermissions = permissions.filter { 
            it.code.endsWith("_READ") 
        }.toMutableSet()
        userRole.permissions = userPermissions
        roleRepository.save(userRole)

        // 4. Initialize Organization
        val mainOrg = organizationRepository.findByNameAndDeletedFalse("Main Organization") ?: organizationRepository.save(
            Organization(
                name = "Main Organization",
                address = "Main Address",
                code = "ORG001"
            )
        )

        // 5. Initialize Admin User
        if (userRepository.findByUsernameAndDeletedFalse("admin") == null) {
            userRepository.save(
                User(
                    username = "admin",
                    password = passwordEncoder.encode("admin123"),
                    firstName = "System",
                    lastName = "Administrator",
                    code = "EMP001",
                    organization = mainOrg,
                    roles = mutableSetOf(adminRole)
                )
            )
        }
    }
}

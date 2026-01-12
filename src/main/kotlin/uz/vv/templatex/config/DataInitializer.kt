package uz.vv.templatex.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uz.vv.templatex.entity.Permission
import uz.vv.templatex.entity.Role
import uz.vv.templatex.enum.PermissionType
import uz.vv.templatex.repository.PermissionRepository
import uz.vv.templatex.repository.RoleRepository

@Configuration
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {

    @Bean
    fun initData() = CommandLineRunner {
        // 1. Initialize Permissions
        val permissions = PermissionType.entries.map { type ->
            permissionRepository.findByCode(type.code) ?: permissionRepository.save(
                Permission(code = type.code, name = type.description)
            )
        }

        // 2. Initialize Roles
        val adminRole = roleRepository.findByCode("ADMIN") ?: roleRepository.save(
            Role(code = "ADMIN", name = "Administrator")
        )
        
        val userRole = roleRepository.findByCode("USER") ?: roleRepository.save(
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
    }
}

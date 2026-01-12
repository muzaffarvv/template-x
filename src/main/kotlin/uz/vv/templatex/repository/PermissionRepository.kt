package uz.vv.templatex.repository

import org.springframework.data.jpa.repository.JpaRepository
import uz.vv.templatex.entity.Permission
import java.util.UUID

interface PermissionRepository : JpaRepository<Permission, UUID> {
    fun findByCode(code: String): Permission?
}

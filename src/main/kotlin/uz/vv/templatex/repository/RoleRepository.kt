package uz.vv.templatex.repository

import org.springframework.data.jpa.repository.JpaRepository
import uz.vv.templatex.entity.Role
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findAllByIdIn(ids: List<UUID>): MutableSet<Role>
}

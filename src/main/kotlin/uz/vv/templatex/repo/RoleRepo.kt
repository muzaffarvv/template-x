package uz.vv.templatex.repo

import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.Role
import java.util.UUID

@Repository
interface RoleRepo : BaseRepo<Role> {
    fun findAllByIdInAndDeletedFalse(ids: Collection<UUID>): MutableSet<Role>
    fun findByCodeAndDeletedFalse(code: String): Role?
}

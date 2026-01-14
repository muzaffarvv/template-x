package uz.vv.templatex.repo

import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.Permission

@Repository
interface PermissionRepo : BaseRepo<Permission> {
    fun findByCodeAndDeletedFalse(code: String): Permission?
}

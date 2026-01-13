package uz.vv.templatex.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.User
import java.util.UUID

@Repository
interface UserRepo : BaseRepo<User> {
    fun findByUsernameAndDeletedFalse(username: String): User?
    fun findByOrganizationIdAndDeletedFalse(organizationId: UUID): List<User>

    @Query("SELECT MAX(CAST(SUBSTRING(u.code, 4) AS int)) FROM User u WHERE u.code LIKE 'EMP%'")
    fun findMaxCodeNumber(): Int?

    fun countByDeletedFalse(): Long
    fun countByStatusAndDeletedFalse(status: uz.vv.templatex.enum.Status): Long
    fun countByOrganizationIdAndDeletedFalse(organizationId: UUID): Int
}
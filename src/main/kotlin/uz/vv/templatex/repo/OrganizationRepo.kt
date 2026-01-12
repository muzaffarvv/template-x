package uz.vv.templatex.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.Organization

@Repository
interface OrganizationRepo : BaseRepo<Organization> {
    fun findByNameAndDeletedFalse(name: String): Organization?

    @Query("SELECT MAX(CAST(SUBSTRING(o.code, 4) AS int)) FROM Organization o WHERE o.code LIKE 'ORG%'")
    fun findMaxCodeNumber(): Int?
}
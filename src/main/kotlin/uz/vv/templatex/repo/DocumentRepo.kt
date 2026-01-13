package uz.vv.templatex.repo

import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.Document
import java.util.UUID

@Repository
interface DocumentRepo : BaseRepo<Document> {
    fun findByOrganizationIdAndDeletedFalse(organizationId: UUID): List<Document>
    fun findByKeyNameAndDeletedFalse(keyName: String): Document?

    fun countByDeletedFalse(): Long
    fun countByOrganizationIdAndDeletedFalse(organizationId: UUID): Int
}
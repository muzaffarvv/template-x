package uz.vv.templatex.repo

import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.DocumentField
import java.util.UUID

@Repository
interface DocumentFieldRepo : BaseRepo<DocumentField> {
    fun findByDocumentIdAndDeletedFalse(documentId: UUID): List<DocumentField>
}
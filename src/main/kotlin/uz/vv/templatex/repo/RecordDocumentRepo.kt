package uz.vv.templatex.repo

import org.springframework.stereotype.Repository
import uz.vv.templatex.base.BaseRepo
import uz.vv.templatex.entity.RecordDocument
import java.util.UUID

@Repository
interface RecordDocumentRepo : BaseRepo<RecordDocument> {
    fun findByUserIdAndDeletedFalse(userId: UUID): List<RecordDocument>
    fun findByDocumentIdAndDeletedFalse(documentId: UUID): List<RecordDocument>
}
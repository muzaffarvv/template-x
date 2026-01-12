package uz.vv.templatex.service

import uz.vv.templatex.base.BaseService
import uz.vv.templatex.dto.RecordDocumentCreateDTO
import uz.vv.templatex.dto.RecordDocumentResponseDTO
import java.util.UUID

interface RecordDocumentService : BaseService<RecordDocumentCreateDTO, Unit, RecordDocumentResponseDTO> {
    fun getRecordsByUser(userId: UUID): List<RecordDocumentResponseDTO>
    fun getRecordsByDocument(documentId: UUID): List<RecordDocumentResponseDTO>
}


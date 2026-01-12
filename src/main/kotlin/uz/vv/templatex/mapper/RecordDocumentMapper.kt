package uz.vv.templatex.mapper

import org.springframework.stereotype.Component
import uz.vv.templatex.base.BaseMapper
import uz.vv.templatex.dto.RecordDocumentResponseDTO
import uz.vv.templatex.entity.RecordDocument

@Component
class RecordDocumentMapper : BaseMapper<RecordDocument, RecordDocumentResponseDTO> {

    override fun toDTO(entity: RecordDocument) = RecordDocumentResponseDTO(
        id = entity.id,
        name = entity.name,
        generatedFileKey = entity.generatedFileKey,
        outputType = entity.outputType,
        fieldValues = entity.fieldValues,
        status = entity.status,
        documentId = entity.document.id,
        documentKeyName = entity.document.keyName,
        documentName = entity.document.name,
        userId = entity.user.id,
        userCode = entity.user.code,
        username = entity.user.username,
        createdAt = entity.createdAt
    )
}
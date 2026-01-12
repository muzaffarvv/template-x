package uz.vv.templatex.mapper

import org.springframework.stereotype.Component
import uz.vv.templatex.base.BaseMapper
import uz.vv.templatex.dto.RecordClassResponseDTO
import uz.vv.templatex.entity.RecordDocument

@Component
class RecordMapper : BaseMapper<RecordDocument, RecordClassResponseDTO> {

    override fun toDTO(entity: RecordDocument) = RecordClassResponseDTO(
        id = entity.id,
        name = entity.name,
        generatedFilePath = entity.generatedFilePath,
        outputType = entity.outputType,
        fieldValues = entity.fieldValues,
        status = entity.status,
        documentId = entity.document.id,
        documentKeyName = entity.document.keyName,
        documentName = entity.document.orgName,
        userId = entity.user.id,
        userCode = entity.user.code,
        username = entity.user.username,
        createdAt = entity.createdAt
    )
}
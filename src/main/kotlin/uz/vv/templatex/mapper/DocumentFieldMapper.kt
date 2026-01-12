package uz.vv.templatex.mapper

import org.springframework.stereotype.Component
import uz.vv.templatex.base.BaseMapper
import uz.vv.templatex.dto.DocumentFieldResponseDTO
import uz.vv.templatex.entity.DocumentField

@Component
class DocumentFieldMapper : BaseMapper<DocumentField, DocumentFieldResponseDTO> {
    override fun toDTO(entity: DocumentField) = DocumentFieldResponseDTO(
        id = entity.id,
        label = entity.label,
        keyName = entity.keyName,
        type = entity.type,
        isRequired = entity.isRequired,
        orderIndex = entity.orderIndex,
        constraints = entity.constraints,
        status = entity.status
    )
}
package uz.vv.templatex.mapper

import org.springframework.stereotype.Component
import uz.vv.templatex.base.BaseMapper
import uz.vv.templatex.dto.DocumentResponseDTO
import uz.vv.templatex.entity.Document
import uz.vv.templatex.repo.DocumentFieldRepo

@Component
class DocumentMapper(
    private val fieldMapper: DocumentFieldMapper,
    private val documentFieldRepo: DocumentFieldRepo
) : BaseMapper<Document, DocumentResponseDTO> {
    override fun toDTO(entity: Document): DocumentResponseDTO {
        // IMPORTANT: OneToMany yo'q, shuning uchun fieldlarni alohida query qilamiz
        val fields = documentFieldRepo.findByDocumentIdAndDeletedFalse(entity.id!!)
            .sortedBy { it.orderIndex }
            .map { fieldMapper.toDTO(it) }

        return DocumentResponseDTO(
            id = entity.id,
            keyName = entity.keyName,
            name = entity.name,
            fileUrl = entity.fileUrl,
            version = entity.version,
            size = entity.size,
            docType = entity.docType,
            status = entity.status,
            organizationId = entity.organization.id,
            organizationCode = entity.organization.code,
            organizationName = entity.organization.name,
            fields = fields,
            createdAt = entity.createdAt
        )
    }
}
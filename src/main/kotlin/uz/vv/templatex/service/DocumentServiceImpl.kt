package uz.vv.templatex.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.vv.templatex.base.BaseServiceImpl
import uz.vv.templatex.dto.DocumentCreateDTO
import uz.vv.templatex.dto.DocumentResponseDTO
import uz.vv.templatex.dto.DocumentUpdateDTO
import uz.vv.templatex.entity.Document
import uz.vv.templatex.entity.DocumentField
import uz.vv.templatex.exception.DocumentNotFoundException
import uz.vv.templatex.exception.OrganizationNotFoundException
import uz.vv.templatex.mapper.DocumentMapper
import uz.vv.templatex.repo.DocumentFieldRepo
import uz.vv.templatex.repo.DocumentRepo
import uz.vv.templatex.repo.OrganizationRepo
import java.util.UUID

@Service
class DocumentServiceImpl(
    repository: DocumentRepo,
    mapper: DocumentMapper,
    private val organizationRepo: OrganizationRepo,
    private val documentFieldRepo: DocumentFieldRepo
) : BaseServiceImpl<
        Document,
        DocumentCreateDTO,
        DocumentUpdateDTO,
        DocumentResponseDTO,
        DocumentMapper,
        DocumentRepo>(
    repository, mapper
), DocumentService {

    override fun validateCreate(dto: DocumentCreateDTO) {
        organizationRepo.findByIdAndDeletedFalse(dto.organizationId)
            ?: throw OrganizationNotFoundException("Organization with id '${dto.organizationId}' not found")
    }

    override fun convertCreateDtoToEntity(dto: DocumentCreateDTO): Document {
        val organization = organizationRepo.findByIdAndDeletedFalse(dto.organizationId)
            ?: throw OrganizationNotFoundException("Organization not found")

        // Auto-generate keyName: 10_char_uuid + originalName
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val keyName = uuid.take(10) + "_${dto.name.replace(" ", "_")}"

        val document = Document(
            name = dto.name,
            fileUrl = dto.fileUrl,
            size = dto.size,
            docType = dto.docType,
            organization = organization,
            keyName = keyName
        )

        return document
    }

    @Transactional
    override fun create(dto: DocumentCreateDTO): DocumentResponseDTO {
        validateCreate(dto)
        val entity = convertCreateDtoToEntity(dto)
        val savedDocument = repository.saveAndRefresh(entity)

        // Create document fields separately
        dto.fields.forEach { fieldDto ->
            val field = DocumentField(
                label = fieldDto.label,
                keyName = fieldDto.keyName,
                type = fieldDto.type,
                isRequired = fieldDto.isRequired,
                orderIndex = fieldDto.orderIndex,
                constraints = fieldDto.constraints,
                document = savedDocument
            )
            documentFieldRepo.saveAndRefresh(field)
        }

        return mapper.toDTO(savedDocument)
    }

    override fun updateEntityFromDto(entity: Document, dto: DocumentUpdateDTO) {
        dto.name?.let { entity.name = it }
        dto.status?.let { entity.status = it }
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): DocumentResponseDTO {
        val entity = getEntityOrNull(id)
            ?: throw DocumentNotFoundException("Document not found: $id")
        return mapper.toDTO(entity)
    }

    override fun update(id: UUID, dto: DocumentUpdateDTO): DocumentResponseDTO {
        val entity = getEntityOrNull(id)
            ?: throw DocumentNotFoundException("Document not found: $id")
        updateEntityFromDto(entity, dto)
        return mapper.toDTO(saveAndRefresh(entity))
    }

    @Transactional(readOnly = true)
    override fun getDocumentsByOrganization(organizationId: UUID): List<DocumentResponseDTO> {
        val documents = repository.findByOrganizationIdAndDeletedFalse(organizationId)
        return mapper.toDTOList(documents)
    }
}
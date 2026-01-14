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

import org.springframework.web.multipart.MultipartFile
import uz.vv.templatex.enum.FieldType
import java.io.FileInputStream
import org.apache.poi.xwpf.usermodel.XWPFDocument
import uz.vv.templatex.enum.Status
import java.time.Instant

@Service
class DocumentServiceImpl(
    repository: DocumentRepo,
    mapper: DocumentMapper,
    private val organizationRepo: OrganizationRepo,
    private val documentFieldRepo: DocumentFieldRepo,
    private val fileStorageService: FileStorageService
) : BaseServiceImpl<
        Document,
        DocumentCreateDTO,
        DocumentUpdateDTO,
        DocumentResponseDTO,
        DocumentMapper,
        DocumentRepo>(
    repository, mapper
), DocumentService {

    @Transactional
    override fun uploadAndSave(file: MultipartFile, organizationId: UUID, name: String): DocumentResponseDTO {
        val filePath = fileStorageService.save(file, organizationId)

        val placeholders = extractPlaceholders(filePath)

        val organization = organizationRepo.findByIdAndDeletedFalse(organizationId)
            ?: throw OrganizationNotFoundException("Organization not found")

        val uuid = UUID.randomUUID().toString().replace("-", "")
        val keyName = uuid.take(10) + "_${name.replace(" ", "_")}"

        val document = Document(
            name = name,
            fileUrl = filePath,
            size = file.size,
            docType = uz.vv.templatex.enum.DocumentType.WORD, // Default to WORD for now as we only support placeholder extraction for Word
            organization = organization,
            keyName = keyName
        )
        val savedDocument = repository.saveAndRefresh(document)

        // 4. Create document fields for each placeholder
        placeholders.forEachIndexed { index, placeholder ->
            val field = DocumentField(
                label = placeholder.replaceFirstChar { it.uppercase() },
                keyName = placeholder,
                type = FieldType.STRING,
                isRequired = true,
                orderIndex = index,
                document = savedDocument
            )
            documentFieldRepo.saveAndRefresh(field)
        }

        return getById(savedDocument.id!!)
    }

    private fun extractPlaceholders(filePath: String): Set<String> {
        val placeholders = mutableSetOf<String>()
        try {
            val file = fileStorageService.getFile(filePath)
            FileInputStream(file).use { fis ->
                val doc = XWPFDocument(fis)
                val regex = Regex("\\$\\{([^}]+)\\}")

                doc.paragraphs.forEach { paragraph ->
                    regex.findAll(paragraph.text).forEach { matchResult ->
                        placeholders.add(matchResult.groupValues[1])
                    }
                }

                doc.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { paragraph ->
                                regex.findAll(paragraph.text).forEach { matchResult ->
                                    placeholders.add(matchResult.groupValues[1])
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error extracting placeholders: ${e.message}")
        }
        return placeholders
    }

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
package uz.vv.templatex.service

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import uz.vv.templatex.base.BaseServiceImpl
import uz.vv.templatex.dto.DocumentCreateDTO
import uz.vv.templatex.dto.DocumentResponseDTO
import uz.vv.templatex.dto.DocumentUpdateDTO
import uz.vv.templatex.entity.Document
import uz.vv.templatex.entity.DocumentField
import uz.vv.templatex.enum.FieldType
import uz.vv.templatex.exception.DocumentNotFoundException
import uz.vv.templatex.exception.OrganizationNotFoundException
import uz.vv.templatex.mapper.DocumentMapper
import uz.vv.templatex.repo.DocumentFieldRepo
import uz.vv.templatex.repo.DocumentRepo
import uz.vv.templatex.repo.OrganizationRepo
import java.io.FileInputStream
import java.util.*

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

    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun uploadAndSave(file: MultipartFile, organizationId: UUID, name: String): DocumentResponseDTO {
        val filePath = fileStorageService.save(file, organizationId)
        val organization = findOrganizationOrThrow(organizationId)

        val document = Document(
            name = name,
            fileUrl = filePath,
            size = file.size,
            docType = uz.vv.templatex.enum.DocumentType.WORD,
            organization = organization,
            keyName = generateDocumentKey(name)
        )
        val savedDocument = repository.saveAndRefresh(document)

        val placeholders = extractPlaceholders(filePath)
        saveDefaultFieldsFromPlaceholders(savedDocument, placeholders)

        return getById(savedDocument.id!!)
    }

    @Transactional
    override fun create(dto: DocumentCreateDTO): DocumentResponseDTO {
        validateCreate(dto)
        val entity = convertCreateDtoToEntity(dto)
        val savedDocument = repository.saveAndRefresh(entity)

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

    override fun convertCreateDtoToEntity(dto: DocumentCreateDTO): Document {
        val organization = findOrganizationOrThrow(dto.organizationId)

        return Document(
            name = dto.name,
            fileUrl = dto.fileUrl,
            size = dto.size,
            docType = dto.docType,
            organization = organization,
            keyName = generateDocumentKey(dto.name)
        )
    }

    private fun findOrganizationOrThrow(organizationId: UUID) =
        organizationRepo.findByIdAndDeletedFalse(organizationId)
            ?: throw OrganizationNotFoundException("Organization not found: $organizationId")

    private fun generateDocumentKey(name: String): String {
        val uuidPart = UUID.randomUUID().toString().replace("-", "").take(10)
        return "${uuidPart}_${name.replace(" ", "_")}"
    }

    private fun saveDefaultFieldsFromPlaceholders(document: Document, placeholders: Set<String>) {
        placeholders.forEachIndexed { index, placeholder ->
            val field = DocumentField(
                label = placeholder.replaceFirstChar { it.uppercase() },
                keyName = placeholder,
                type = FieldType.STRING,
                isRequired = true,
                orderIndex = index,
                document = document
            )
            documentFieldRepo.saveAndRefresh(field)
        }
    }

    private fun extractPlaceholders(filePath: String): Set<String> {
        val placeholders = mutableSetOf<String>()
        val regex = Regex("\\$\\{([^}]+)}")

        try {
            val file = fileStorageService.getFile(filePath)
            FileInputStream(file).use { fis ->
                val doc = XWPFDocument(fis)

                doc.paragraphs.forEach { p ->
                    regex.findAll(p.text).forEach { placeholders.add(it.groupValues[1]) }
                }

                doc.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { p ->
                                regex.findAll(p.text).forEach { placeholders.add(it.groupValues[1]) }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error extracting placeholders from document: ${e.message}")
        }
        return placeholders
    }

    override fun validateCreate(dto: DocumentCreateDTO) {
        if (!organizationRepo.existsByIdAndDeletedFalse(dto.organizationId)) {
            throw OrganizationNotFoundException("Organization with id '${dto.organizationId}' not found")
        }
    }

    override fun updateEntityFromDto(entity: Document, dto: DocumentUpdateDTO) {
        dto.name?.let { entity.name = it }
        dto.status?.let { entity.status = it }
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): DocumentResponseDTO {
        val entity = getEntityOrNull(id) ?: throw DocumentNotFoundException("Document not found: $id")
        return mapper.toDTO(entity)
    }

    override fun update(id: UUID, dto: DocumentUpdateDTO): DocumentResponseDTO {
        val entity = getEntityOrNull(id) ?: throw DocumentNotFoundException("Document not found: $id")
        updateEntityFromDto(entity, dto)
        return mapper.toDTO(saveAndRefresh(entity))
    }

    @Transactional(readOnly = true)
    override fun getDocumentsByOrganization(organizationId: UUID): List<DocumentResponseDTO> {
        return mapper.toDTOList(repository.findByOrganizationIdAndDeletedFalse(organizationId))
    }
}
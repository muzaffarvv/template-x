package uz.vv.templatex.service

import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.vv.templatex.base.BaseServiceImpl
import uz.vv.templatex.dto.RecordDocumentCreateDTO
import uz.vv.templatex.dto.RecordDocumentResponseDTO
import uz.vv.templatex.entity.RecordDocument
import uz.vv.templatex.exception.DocumentNotFoundException
import uz.vv.templatex.exception.GeneratedDocumentNotFoundException
import uz.vv.templatex.exception.UserNotFoundException
import uz.vv.templatex.mapper.RecordDocumentMapper
import uz.vv.templatex.repo.DocumentRepo
import uz.vv.templatex.repo.RecordDocumentRepo
import uz.vv.templatex.repo.UserRepo

@Service
class RecordDocumentServiceImpl(
    repository: RecordDocumentRepo,
    mapper: RecordDocumentMapper,
    private val documentRepo: DocumentRepo,
    private val userRepo: UserRepo
) : BaseServiceImpl<
        RecordDocument,
        RecordDocumentCreateDTO,
        Unit,
        RecordDocumentResponseDTO,
        RecordDocumentMapper,
        RecordDocumentRepo>(
    repository, mapper
), RecordDocumentService {

    override fun validateCreate(dto: RecordDocumentCreateDTO) {
        documentRepo.findByIdAndDeletedFalse(dto.documentId)
            ?: throw DocumentNotFoundException("Document not found: ${dto.documentId}")

        userRepo.findByIdAndDeletedFalse(dto.userId)
            ?: throw UserNotFoundException("User not found: ${dto.userId}")
    }

    override fun convertCreateDtoToEntity(dto: RecordDocumentCreateDTO): RecordDocument {
        val document = documentRepo.findByIdAndDeletedFalse(dto.documentId)
            ?: throw DocumentNotFoundException("Document not found: ${dto.documentId}")

        val user = userRepo.findByIdAndDeletedFalse(dto.userId)
            ?: throw UserNotFoundException("User not found: ${dto.userId}")

        return RecordDocument(
            name = dto.name,
            generatedFileKey = dto.generatedFileKey,
            outputType = dto.outputType,
            fieldValues = dto.fieldValues,
            document = document,
            user = user
        )
    }

    override fun updateEntityFromDto(entity: RecordDocument, dto: Unit) {
        // RecordClass updates not allowed in this implementation
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): RecordDocumentResponseDTO {
        val entity = getEntityOrNull(id)
            ?: throw GeneratedDocumentNotFoundException("Record not found: $id")
        return mapper.toDTO(entity)
    }

    @Transactional
    override fun update(id: UUID, dto: Unit): RecordDocumentResponseDTO {
        throw UnsupportedOperationException("Update not supported for generated records")
    }

    @Transactional(readOnly = true)
    override fun getRecordsByUser(userId: UUID): List<RecordDocumentResponseDTO> {
        val records = repository.findByUserIdAndDeletedFalse(userId)
        return mapper.toDTOList(records)
    }

    @Transactional(readOnly = true)
    override fun getRecordsByDocument(documentId: UUID): List<RecordDocumentResponseDTO> {
        val records = repository.findByDocumentIdAndDeletedFalse(documentId)
        return mapper.toDTOList(records)
    }
}
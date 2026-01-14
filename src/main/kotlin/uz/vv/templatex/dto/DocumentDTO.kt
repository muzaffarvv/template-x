package uz.vv.templatex.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min
import uz.vv.templatex.base.BaseDTO
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.enum.Status
import java.time.Instant
import java.util.UUID

data class DocumentCreateDTO(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    @field:NotBlank
    @field:Size(max = 500)
    val fileUrl: String,

    @field:Min(0)
    val size: Long,

    @field:NotNull
    val docType: DocumentType,

    @field:NotNull
    val organizationId: UUID,

    val fields: List<DocumentFieldCreateDTO> = emptyList()
)

data class DocumentUpdateDTO(
    @field:Size(max = 200)
    val name: String? = null,

    val status: Status? = null
)

data class DocumentResponseDTO(
    override val id: UUID?,
    val keyName: String,
    val name: String,
    val fileUrl: String,
    val version: Int,
    val size: Long,
    val docType: DocumentType,
    val status: Status,
    val organizationId: UUID?,
    val organizationCode: String?,
    val organizationName: String?,
    val fields: List<DocumentFieldResponseDTO>,
    val createdAt: Instant?
) : BaseDTO

data class FileUploadResponseDTO(
    val filePath: String,
    val fileName: String,
    val size: Long,
    val message: String = "File uploaded successfully"
)

data class MultiFileUploadResponseDTO(
    val uploadedFiles: List<FileUploadResponseDTO>,
    val totalCount: Int
)

data class DocumentGenerationResponseDTO(
    val filePath: String,
    val fileName: String,
    val outputType: DocumentType,
    val message: String = "Document generated successfully"
)

data class DocumentUploadResponseDTO(
    val documentId: UUID,
    val name: String,
    val fileUrl: String,
    val placeholders: List<String>,
    val placeholdersCopyText: String,
    val fields: List<DocumentFieldResponseDTO>
)

data class DocumentGenerationRequestDTO(
    @field:NotNull(message = "documentId is required")
    val documentId: UUID,

    @field:NotNull(message = "fieldValues is required")
    @field:Size(min = 1, message = "At least one field value is required")
    val fieldValues: Map<@NotBlank String, @NotBlank String>,

    @field:NotNull(message = "outputType is required")
    val outputType: DocumentType,

    @field:NotNull(message = "userId is required")
    val userId: UUID
)

data class GenerateDocumentResponseDTO(
    val recordId: UUID,
    val documentId: UUID,
    val filePath: String,
    val availableFormats: List<DocumentType>,
    val message: String
)

data class UserRecordHistoryDTO(
    val recordId: UUID,
    val documentName: String,
    val generatedDate: Instant,
    val outputType: DocumentType,
    val availableFormats: List<DocumentType>
)

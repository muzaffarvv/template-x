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

data class DocumentGenerationRequestDTO(
    @field:NotNull
    val documentId: UUID,

    @field:NotNull
    val outputType: DocumentType,

    @field:NotNull
    val fieldValues: Map<String, String>,

    @field:NotNull
    val userId: UUID
)

data class DocumentGenerationResponseDTO(
    val filePath: String,
    val fileName: String,
    val outputType: DocumentType,
    val message: String = "Document generated successfully"
)
package uz.vv.templatex.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uz.vv.templatex.base.BaseDTO
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.enum.Status
import java.time.Instant
import java.util.UUID

data class RecordDocumentCreateDTO(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    @field:NotBlank
    val generatedFileKey: String,

    @field:NotNull
    val outputType: DocumentType,

    @field:NotNull
    @field:Size(min = 1)
    val fieldValues: Map<@NotBlank String, @NotBlank String>,

    @field:NotNull
    val documentId: UUID,

    @field:NotNull
    val userId: UUID
)

data class RecordDocumentResponseDTO(
    override val id: UUID?,
    val name: String,
    val generatedFileKey: String,
    val outputType: DocumentType,
    val fieldValues: Map<String, String>,
    val status: Status,
    val documentId: UUID?,
    val documentKeyName: String?,
    val documentName: String?,
    val userId: UUID?,
    val userCode: String?,
    val username: String?,
    val createdAt: Instant?
) : BaseDTO
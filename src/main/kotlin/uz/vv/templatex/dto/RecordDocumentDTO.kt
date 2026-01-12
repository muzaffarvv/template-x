package uz.vv.templatex.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uz.vv.templatex.base.BaseDTO
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.enum.Status
import java.time.Instant
import java.util.UUID

data class RecordClassCreateDTO(
    @field:NotBlank
    @field:Size(max = 200)
    val name: String,

    @field:NotBlank
    @field:Size(max = 500)
    val generatedFilePath: String,

    @field:NotNull
    val outputType: DocumentType,

    @field:NotNull
    val fieldValues: Map<String, String>,

    @field:NotNull
    val documentId: UUID,

    @field:NotNull
    val userId: UUID
)

data class RecordClassResponseDTO(
    override val id: UUID?,
    val name: String,
    val generatedFilePath: String,
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
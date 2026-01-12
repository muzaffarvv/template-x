package uz.vv.templatex.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import uz.vv.templatex.base.BaseDTO
import uz.vv.templatex.enum.FieldType
import uz.vv.templatex.enum.Status
import java.util.UUID

data class DocumentFieldCreateDTO(
    @field:NotBlank
    @field:Size(max = 72)
    val label: String,

    @field:NotBlank
    @field:Size(max = 60)
    @field:Pattern(regexp = "^[a-z][a-z0-9_]*$")
    val keyName: String,

    @field:NotNull
    val type: FieldType,

    val isRequired: Boolean = false,

    @field:Min(0)
    val orderIndex: Int = 0,

    val constraints: Map<String, String>? = null
)

data class DocumentFieldUpdateDTO(
    @field:Size(max = 72)
    val label: String? = null,

    val type: FieldType? = null,

    val isRequired: Boolean? = null,

    @field:Min(0)
    val orderIndex: Int? = null,

    val constraints: Map<String, String>? = null,

    val status: Status? = null
)

data class DocumentFieldResponseDTO(
    override val id: UUID?,
    val label: String,
    val keyName: String,
    val type: FieldType,
    val isRequired: Boolean,
    val orderIndex: Int,
    val constraints: Map<String, String>?,
    val status: Status
) : BaseDTO

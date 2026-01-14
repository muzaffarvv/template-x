package uz.vv.templatex.dto

import uz.vv.templatex.base.BaseDTO
import java.util.UUID

data class OrganizationDTO(
    override val id: UUID? = null,
    val name: String,
    val address: String,
    val tagline: String? = null,
    val code: String? = null
) : BaseDTO

data class OrganizationCreateDTO(
    val name: String,
    val address: String,
    val tagline: String? = null
)

data class OrganizationUpdateDTO(
    val name: String? = null,
    val address: String? = null,
    val tagline: String? = null
)

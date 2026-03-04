package uz.vv.templatex.service

import org.springframework.stereotype.Service
import uz.vv.templatex.base.BaseServiceImpl
import uz.vv.templatex.dto.OrganizationCreateDTO
import uz.vv.templatex.dto.OrganizationDTO
import uz.vv.templatex.dto.OrganizationUpdateDTO
import uz.vv.templatex.entity.Organization
import uz.vv.templatex.exception.OrganizationNotFoundException
import uz.vv.templatex.mapper.OrganizationMapper
import uz.vv.templatex.repo.OrganizationRepo
import java.util.*

@Service
class OrganizationServiceImpl(
    override val repository: OrganizationRepo,
    override val mapper: OrganizationMapper
) : BaseServiceImpl<Organization, OrganizationCreateDTO, OrganizationUpdateDTO, OrganizationDTO, OrganizationMapper, OrganizationRepo>(
    repository, mapper
), OrganizationService {

    override fun convertCreateDtoToEntity(dto: OrganizationCreateDTO): Organization {
        validateOrganizationData(dto.name, dto.address)

        return Organization(
            name = dto.name,
            address = dto.address,
            tagline = dto.tagline,
            code = generateOrganizationCode()
        )
    }

    override fun updateEntityFromDto(entity: Organization, dto: OrganizationUpdateDTO) {
        dto.name?.let {
            validateName(it)
            entity.name = it
        }
        dto.address?.let {
            validateAddress(it)
            entity.address = it
        }
        dto.tagline?.let { entity.tagline = it }
    }

    override fun getById(id: UUID): OrganizationDTO {
        val entity = findEntityOrThrow(id)
        return mapper.toDTO(entity)
    }

    override fun update(id: UUID, dto: OrganizationUpdateDTO): OrganizationDTO {
        val entity = findEntityOrThrow(id)
        updateEntityFromDto(entity, dto)
        return mapper.toDTO(repository.save(entity))
    }

    private fun findEntityOrThrow(id: UUID): Organization {
        return getEntityOrNull(id) ?: throw OrganizationNotFoundException("Organization not found: $id")
    }

    private fun generateOrganizationCode(): String {
        val nextCodeNumber = (repository.findMaxCodeNumber() ?: 0) + 1
        return "ORG${nextCodeNumber.toString().padStart(3, '0')}"
    }

    private fun validateOrganizationData(name: String, address: String?) {
        validateName(name)
        address?.let { validateAddress(it) }
    }

    private fun validateName(name: String) {
        // Bu yerda regex yoki boshqa name validatsiyasi bo'ladi
    }

    private fun validateAddress(address: String) {
        // Bu yerda address validatsiyasi bo'ladi
    }
}
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
        val nextCodeNumber = (repository.findMaxCodeNumber() ?: 0) + 1
        val code = "ORG${nextCodeNumber.toString().padStart(3, '0')}"

        // validation name address regex
        
        return Organization(
            name = dto.name,
            address = dto.address,
            tagline = dto.tagline,
            code = code
        )
    }

    override fun updateEntityFromDto(entity: Organization, dto: OrganizationUpdateDTO) {
        dto.name?.let { entity.name = it }
        dto.address?.let { entity.address = it }
        dto.tagline?.let { entity.tagline = it }
    }

    override fun getById(id: UUID): OrganizationDTO {
        val entity = getEntityOrNull(id) ?: throw OrganizationNotFoundException("Organization not found: $id")
        return mapper.toDTO(entity)
    }

    override fun update(id: UUID, dto: OrganizationUpdateDTO): OrganizationDTO {
        // validate name address
        val entity = getEntityOrNull(id) ?: throw OrganizationNotFoundException("Organization not found: $id")
        updateEntityFromDto(entity, dto)
        return mapper.toDTO(repository.save(entity))
    }
}

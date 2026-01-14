package uz.vv.templatex.mapper

import org.springframework.stereotype.Component
import uz.vv.templatex.base.BaseMapper
import uz.vv.templatex.dto.OrganizationDTO
import uz.vv.templatex.entity.Organization

@Component
class OrganizationMapper : BaseMapper<Organization, OrganizationDTO> {
    override fun toDTO(entity: Organization): OrganizationDTO {
        return OrganizationDTO(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            tagline = entity.tagline,
            code = entity.code
        )
    }
}

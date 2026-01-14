package uz.vv.templatex.service

import uz.vv.templatex.base.BaseService
import uz.vv.templatex.dto.OrganizationCreateDTO
import uz.vv.templatex.dto.OrganizationDTO
import uz.vv.templatex.dto.OrganizationUpdateDTO

interface OrganizationService : BaseService<OrganizationCreateDTO, OrganizationUpdateDTO, OrganizationDTO>

package uz.vv.templatex.repository

import org.springframework.data.jpa.repository.JpaRepository
import uz.vv.templatex.entity.Organization
import java.util.UUID

interface OrganizationRepository : JpaRepository<Organization, UUID>

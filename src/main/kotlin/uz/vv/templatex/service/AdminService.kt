package uz.vv.templatex.service

import uz.vv.templatex.dto.AdminStatisticsDTO
import uz.vv.templatex.dto.AdminOrganizationSummaryDTO
import java.util.UUID

interface AdminService {
    fun getStatistics(): AdminStatisticsDTO
    fun getOrganizationSummaries(): List<AdminOrganizationSummaryDTO>
    fun deleteUser(userId: UUID)
    fun deleteOrganization(organizationId: UUID)
}

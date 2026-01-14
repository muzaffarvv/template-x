package uz.vv.templatex.controller

import org.springframework.web.bind.annotation.*
import uz.vv.templatex.dto.AdminStatisticsDTO
import uz.vv.templatex.dto.AdminOrganizationSummaryDTO
import uz.vv.templatex.service.AdminService
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/statistics")
    fun getStatistics(): AdminStatisticsDTO {
        return adminService.getStatistics()
    }

    @GetMapping("/organizations/summary")
    fun getOrganizationSummaries(): List<AdminOrganizationSummaryDTO> {
        return adminService.getOrganizationSummaries()
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable userId: UUID) {
        adminService.deleteUser(userId)
    }

    @DeleteMapping("/organizations/{organizationId}")
    fun deleteOrganization(@PathVariable organizationId: UUID) {
        adminService.deleteOrganization(organizationId)
    }
}

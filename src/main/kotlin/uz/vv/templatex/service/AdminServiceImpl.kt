package uz.vv.templatex.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.vv.templatex.dto.AdminStatisticsDTO
import uz.vv.templatex.dto.AdminOrganizationSummaryDTO
import uz.vv.templatex.enum.Status
import uz.vv.templatex.exception.OrganizationNotFoundException
import uz.vv.templatex.exception.UserNotFoundException
import uz.vv.templatex.repo.*
import java.util.UUID

@Service
class AdminServiceImpl(
    private val userRepo: UserRepo,
    private val organizationRepo: OrganizationRepo,
    private val documentRepo: DocumentRepo,
    private val recordDocumentRepo: RecordDocumentRepo
) : AdminService {

    @Transactional(readOnly = true)
    override fun getStatistics(): AdminStatisticsDTO {
        return AdminStatisticsDTO(
            organizationCount = organizationRepo.countByDeletedFalse(),
            userCount = userRepo.countByDeletedFalse(),
            documentCount = documentRepo.countByDeletedFalse(),
            generatedDocumentCount = recordDocumentRepo.countByDeletedFalse(),
            activeUserCount = userRepo.countByStatusAndDeletedFalse(Status.ACTIVE)
        )
    }

    @Transactional(readOnly = true)
    override fun getOrganizationSummaries(): List<AdminOrganizationSummaryDTO> {
        return organizationRepo.findAllNotDeleted().map { org ->
            AdminOrganizationSummaryDTO(
                organizationId = org.id!!,
                name = org.name,
                userCount = userRepo.countByOrganizationIdAndDeletedFalse(org.id!!),
                documentCount = documentRepo.countByOrganizationIdAndDeletedFalse(org.id!!)
            )
        }
    }

    @Transactional
    override fun deleteUser(userId: UUID) {
        userRepo.trash(userId) ?: throw UserNotFoundException("User not found: $userId")
    }

    @Transactional
    override fun deleteOrganization(organizationId: UUID) {
        organizationRepo.trash(organizationId) ?: throw OrganizationNotFoundException("Organization not found: $organizationId")
    }
}

package uz.vv.templatex.dto

data class AdminStatisticsDTO(
    val organizationCount: Long,
    val userCount: Long,
    val documentCount: Long,
    val generatedDocumentCount: Long,
    val activeUserCount: Long
)

data class AdminOrganizationSummaryDTO(
    val organizationId: java.util.UUID,
    val name: String,
    val userCount: Int,
    val documentCount: Int
)

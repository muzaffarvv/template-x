package uz.vv.templatex.service

import uz.vv.templatex.base.BaseService
import uz.vv.templatex.dto.DocumentCreateDTO
import uz.vv.templatex.dto.DocumentResponseDTO
import uz.vv.templatex.dto.DocumentUpdateDTO
import java.util.UUID
import org.springframework.web.multipart.MultipartFile

interface DocumentService : BaseService<DocumentCreateDTO, DocumentUpdateDTO, DocumentResponseDTO> {
    fun getDocumentsByOrganization(organizationId: UUID): List<DocumentResponseDTO>

    fun uploadAndSave(
        file: MultipartFile,
        organizationId: UUID,
        name: String
    ): DocumentResponseDTO
}
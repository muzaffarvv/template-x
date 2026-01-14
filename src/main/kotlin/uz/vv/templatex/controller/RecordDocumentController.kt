package uz.vv.templatex.controller

import org.springframework.web.bind.annotation.*
import uz.vv.templatex.dto.RecordDocumentResponseDTO
import uz.vv.templatex.service.RecordDocumentService
import java.util.UUID

@RestController
@RequestMapping("/api/records")
class RecordDocumentController(
    private val recordDocumentService: RecordDocumentService
) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): RecordDocumentResponseDTO =
        recordDocumentService.getById(id)

    @GetMapping
    fun getAll(): List<RecordDocumentResponseDTO> =
        recordDocumentService.getAll()

    @GetMapping("/user/{userId}")
    fun getByUser(@PathVariable userId: UUID): List<RecordDocumentResponseDTO> =
        recordDocumentService.getRecordsByUser(userId)

    @GetMapping("/document/{documentId}")
    fun getByDocument(@PathVariable documentId: UUID): List<RecordDocumentResponseDTO> =
        recordDocumentService.getRecordsByDocument(documentId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        recordDocumentService.delete(id)
}

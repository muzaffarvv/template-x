package uz.vv.templatex.controller

import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.vv.templatex.dto.DocumentGenerationRequestDTO
import uz.vv.templatex.dto.DocumentGenerationResponseDTO
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.exception.ResponseVO
import uz.vv.templatex.service.DocumentGenerationService

@RestController
@RequestMapping("/api/generate")
class DocumentGenerationController(
    private val documentGenerationService: DocumentGenerationService
) {

    /**
     * Generate document from template
     * Template'dan hujjat generatsiya qilish
     */
    @PostMapping("/document")
    fun generateDocument(
        @Valid @RequestBody request: DocumentGenerationRequestDTO
    ): ResponseEntity<ResponseVO<DocumentGenerationResponseDTO>> {

        val (filePath, _) = documentGenerationService.generateDocument(
            documentId = request.documentId,
            fieldValues = request.fieldValues,
            outputType = request.outputType,
            userId = request.userId
        )

        val fileName = filePath.substringAfterLast("/")

        val response = DocumentGenerationResponseDTO(
            filePath = filePath,
            fileName = fileName,
            outputType = request.outputType
        )

        return ResponseEntity.ok(ResponseVO(status = 200, data = response))
    }

    /**
     * Generate and download document
     * Hujjat generatsiya qilish va yuklab olish
     */
    @PostMapping("/document/download")
    fun generateAndDownloadDocument(
        @Valid @RequestBody request: DocumentGenerationRequestDTO
    ): ResponseEntity<ByteArray> {

        val (_, content) = documentGenerationService.generateDocument(
            documentId = request.documentId,
            fieldValues = request.fieldValues,
            outputType = request.outputType,
            userId = request.userId
        )

        val extension = when (request.outputType) {
            DocumentType.WORD -> "docx"
            DocumentType.PDF -> "pdf"
            DocumentType.PNG -> "png"
            else -> "bin"
        }

        val contentType = when (request.outputType) {
            DocumentType.WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            DocumentType.PDF -> "application/pdf"
            DocumentType.PNG -> "image/png"
            else -> "application/octet-stream"
        }

        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(contentType)
        headers.setContentDispositionFormData("attachment", "document.$extension")

        return ResponseEntity.ok()
            .headers(headers)
            .body(content)
    }
}
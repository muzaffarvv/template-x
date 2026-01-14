package uz.vv.templatex.controller


import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import uz.vv.templatex.dto.*
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.exception.ResponseVO
import uz.vv.templatex.service.DocumentGenerationService
import uz.vv.templatex.service.DocumentService
import uz.vv.templatex.service.RecordDocumentService
import java.util.UUID

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentService: DocumentService,
    private val documentGenerationService: DocumentGenerationService,
    private val recordDocumentService: RecordDocumentService
) {

    /**
     * 1. Upload Word template and auto-detect placeholders
     * POST /api/documents/upload
     */
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadTemplate(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("organizationId") organizationId: UUID,
        @RequestParam("name") name: String
    ): ResponseEntity<ResponseVO<DocumentUploadResponseDTO>> {

        val response = documentService.uploadAndSave(file, organizationId, name)

        // Format placeholders for easy copy-paste
        val placeholdersCopyText = response.fields
            .sortedBy { it.orderIndex }
            .joinToString("\n") { "\${${it.keyName}}" }

        val uploadResponse = DocumentUploadResponseDTO(
            documentId = response.id!!,
            name = response.name,
            fileUrl = response.fileUrl,
            placeholders = response.fields.map { it.keyName },
            placeholdersCopyText = placeholdersCopyText,
            fields = response.fields
        )

        return ResponseEntity.ok(ResponseVO(status = 200, data = uploadResponse))
    }

    /**
     * 2. Get document details with placeholders
     * GET /api/documents/{id}
     */
    @GetMapping("/{id}")
    fun getDocument(@PathVariable id: UUID): ResponseEntity<ResponseVO<DocumentResponseDTO>> {
        val document = documentService.getById(id)
        return ResponseEntity.ok(ResponseVO(status = 200, data = document))
    }

    /**
     * 3. Generate document from template
     * POST /api/documents/generate
     */
    @PostMapping("/generate")
    fun generateDocument(
        @Valid @RequestBody request: DocumentGenerationRequestDTO
    ): ResponseEntity<ResponseVO<GenerateDocumentResponseDTO>> {

        // Generate document and save record
        val (filePath, _) = documentGenerationService.generateDocument(
            documentId = request.documentId,
            fieldValues = request.fieldValues,
            outputType = request.outputType,
            userId = request.userId
        )

        // Save record to database
        val recordCreateDTO = RecordDocumentCreateDTO(
            name = "Generated_${System.currentTimeMillis()}",
            generatedFileKey = filePath,
            outputType = request.outputType,
            fieldValues = request.fieldValues,
            documentId = request.documentId,
            userId = request.userId
        )

        val recordResponse = recordDocumentService.create(recordCreateDTO)

        val response = GenerateDocumentResponseDTO(
            recordId = recordResponse.id!!,
            documentId = request.documentId,
            filePath = filePath,
            availableFormats = listOf(DocumentType.WORD, DocumentType.PDF, DocumentType.PNG),
            message = "Hujjat muvaffaqiyatli yaratildi. Siz uni WORD, PDF yoki PNG formatida yuklab olishingiz mumkin."
        )

        return ResponseEntity.ok(ResponseVO(status = 200, data = response))
    }

    /**
     * 4. Download generated document in specified format
     * GET /api/records/{id}/download?format=WORD|PDF|PNG
     */
    @GetMapping("/records/{id}/download")
    fun downloadGeneratedDocument(
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "WORD") format: DocumentType
    ): ResponseEntity<ByteArray> {

        val record = recordDocumentService.getById(id)

        // Get or generate file in requested format
        val content = documentGenerationService.getOrGenerateFormat(
            recordId = id,
            filePath = record.generatedFileKey,
            fieldValues = record.fieldValues,
            documentId = record.documentId!!,
            userId = record.userId!!,
            requestedFormat = format
        )

        val extension = when (format) {
            DocumentType.WORD -> "docx"
            DocumentType.PDF -> "pdf"
            DocumentType.PNG -> "png"
            else -> "bin"
        }

        val contentType = when (format) {
            DocumentType.WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            DocumentType.PDF -> "application/pdf"
            DocumentType.PNG -> "image/png"
            else -> "application/octet-stream"
        }

        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(contentType)
        headers.setContentDispositionFormData("attachment", "${record.name}.$extension")

        return ResponseEntity.ok()
            .headers(headers)
            .body(content)
    }

    /**
     * 5. Get user's document generation history
     * GET /api/records/user/{userId}
     */
    @GetMapping("/records/user/{userId}")
    fun getUserRecords(
        @PathVariable userId: UUID
    ): ResponseEntity<ResponseVO<List<UserRecordHistoryDTO>>> {

        val records = recordDocumentService.getRecordsByUser(userId)

        val history = records.map { record ->
            UserRecordHistoryDTO(
                recordId = record.id!!,
                documentName = record.documentName ?: "Unknown",
                generatedDate = record.createdAt!!,
                outputType = record.outputType,
                availableFormats = listOf(DocumentType.WORD, DocumentType.PDF, DocumentType.PNG)
            )
        }

        return ResponseEntity.ok(ResponseVO(status = 200, data = history))
    }

    /**
     * Get documents by organization
     * GET /api/documents/organization/{organizationId}
     */
    @GetMapping("/organization/{organizationId}")
    fun getDocumentsByOrganization(
        @PathVariable organizationId: UUID
    ): ResponseEntity<ResponseVO<List<DocumentResponseDTO>>> {
        val documents = documentService.getDocumentsByOrganization(organizationId)
        return ResponseEntity.ok(ResponseVO(status = 200, data = documents))
    }
}

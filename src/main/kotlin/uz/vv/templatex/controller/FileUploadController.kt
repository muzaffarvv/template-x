package uz.vv.templatex.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import uz.vv.templatex.dto.FileUploadResponseDTO
import uz.vv.templatex.dto.MultiFileUploadResponseDTO
import uz.vv.templatex.exception.ResponseVO
import uz.vv.templatex.service.FileStorageService
import java.util.UUID

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileStorageService: FileStorageService
) {

    /**
     * Upload single file
     * Bitta fayl yuklash
     */
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("organizationId") organizationId: UUID
    ): ResponseEntity<ResponseVO<FileUploadResponseDTO>> {

        val filePath = fileStorageService.save(file, organizationId)

        val response = FileUploadResponseDTO(
            filePath = filePath,
            fileName = file.originalFilename ?: "unknown",
            size = file.size
        )

        return ResponseEntity.ok(ResponseVO(status = 200, data = response))
    }

    /**
     * Upload multiple files
     * Ko'p fayllarni yuklash
     */
    @PostMapping("/upload/multiple", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadMultipleFiles(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("organizationId") organizationId: UUID
    ): ResponseEntity<ResponseVO<MultiFileUploadResponseDTO>> {

        val uploadedFiles = files.mapNotNull { file ->
            if (!file.isEmpty) {
                val filePath = fileStorageService.save(file, organizationId)
                FileUploadResponseDTO(
                    filePath = filePath,
                    fileName = file.originalFilename ?: "unknown",
                    size = file.size
                )
            } else null
        }

        val response = MultiFileUploadResponseDTO(
            uploadedFiles = uploadedFiles,
            totalCount = uploadedFiles.size
        )

        return ResponseEntity.ok(ResponseVO(status = 200, data = response))
    }

    /**
     * Delete file
     * Faylni o'chirish
     */
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("filePath") filePath: String): ResponseEntity<ResponseVO<String>> {
        fileStorageService.delete(filePath)
        return ResponseEntity.ok(ResponseVO(status = 200, data = "File deleted successfully"))
    }
}
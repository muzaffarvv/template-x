package uz.vv.templatex.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uz.vv.templatex.exception.FileGenerationException
import uz.vv.templatex.exception.FileUploadException
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileStorageService(
    @Value("\${file.upload-dir:uploads}")
    private val uploadDir: String,
    @Value("\${file.generated-dir:generated}")
    private val generatedDir: String
) {

    init {
        createDirectories()
    }

    private fun createDirectories() {
        val uploadPath = Paths.get(System.getProperty("user.home"), uploadDir)
        val generatedPath = Paths.get(System.getProperty("user.home"), generatedDir)
        Files.createDirectories(uploadPath)
        Files.createDirectories(generatedPath)
    }

    /**
     * SAVE single file
     * Faylni diskga saqlaydi
     * KeyName: unikal nom (UUID 12 char + originalName)
     */
    fun save(file: MultipartFile, organizationId: UUID): String {
        try {
            if (file.isEmpty) throw FileUploadException("File is empty")

            val originalName = file.originalFilename ?: throw FileUploadException("Original filename is null")
            val shortId = UUID.randomUUID().toString().replace("-", "").take(12)
            val keyName = "${shortId}_$originalName"

            // Organization papkasini yaratish
            val orgPath = Paths.get(System.getProperty("user.home"), uploadDir, organizationId.toString())
            Files.createDirectories(orgPath)

            // Faylni saqlash
            val target = orgPath.resolve(keyName)
            Files.copy(file.inputStream, target, StandardCopyOption.REPLACE_EXISTING)

            return target.toString()
        } catch (e: Exception) {
            throw FileUploadException("Failed to upload file: ${e.message}")
        }
    }

    /**
     * SAVE multiple files
     */
    fun saveAll(files: List<MultipartFile>, organizationId: UUID): List<String> {
        val savedPaths = mutableListOf<String>()
        if (files.isEmpty()) return savedPaths

        files.forEach { file ->
            if (!file.isEmpty) {
                savedPaths.add(save(file, organizationId))
            }
        }

        return savedPaths
    }

    /**
     * GET file from disk
     */
    fun getFile(filePath: String): File {
        val file = File(filePath)
        if (!file.exists()) {
            throw FileUploadException("File not found: $filePath")
        }
        return file
    }

    /**
     * DELETE file from disk
     */
    fun delete(filePath: String?) {
        if (filePath == null) return
        try {
            val path = Paths.get(filePath)
            Files.deleteIfExists(path)
        } catch (e: Exception) {
            // Log error but don't throw
            println("Failed to delete file: ${e.message}")
        }
    }

    /**
     * SAVE generated file
     * Generated fayllarni user papkasiga saqlaydi
     */
    fun saveGeneratedFile(content: ByteArray, fileName: String, userId: UUID): String {
        try {
            val userPath = Paths.get(System.getProperty("user.home"), generatedDir, userId.toString())
            Files.createDirectories(userPath)

            val shortId = UUID.randomUUID().toString().replace("-", "").take(12)
            val uniqueFileName = "${shortId}_$fileName"
            val filePath = userPath.resolve(uniqueFileName)

            FileOutputStream(filePath.toFile()).use { it.write(content) }

            return filePath.toString()
        } catch (e: Exception) {
            throw FileGenerationException("Failed to save generated file: ${e.message}")
        }
    }

    /**
     * DELETE all files in a folder
     */
    fun deleteAllByOrganization(organizationId: UUID) {
        try {
            val orgPath = Paths.get(System.getProperty("user.home"), uploadDir, organizationId.toString())
            if (Files.exists(orgPath)) {
                Files.walk(orgPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach { Files.deleteIfExists(it) }
            }
        } catch (e: Exception) {
            println("Failed to delete organization files: ${e.message}")
        }
    }
}
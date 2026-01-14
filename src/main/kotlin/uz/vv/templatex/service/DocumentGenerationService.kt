package uz.vv.templatex.service

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document as PdfLayoutDocument
import com.itextpdf.layout.element.Paragraph
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.stereotype.Service
import uz.vv.templatex.repo.DocumentRepo
import uz.vv.templatex.enum.DocumentType
import uz.vv.templatex.exception.DocumentNotFoundException
import uz.vv.templatex.exception.FileGenerationException
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

@Service
class DocumentGenerationService(
    private val documentRepo: DocumentRepo,
    private val fileStorageService: FileStorageService
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        .withZone(ZoneId.systemDefault())

    /**
     * Generate document from template
     * Template'dan yangi hujjat generatsiya qiladi
     */
    fun generateDocument(
        documentId: UUID,
        fieldValues: Map<String, String>,
        outputType: DocumentType,
        userId: UUID
    ): Pair<String, ByteArray> {

        val document = documentRepo.findByIdAndDeletedFalse(documentId)
            ?: throw DocumentNotFoundException("Document with id '$documentId' not found")

        // Get a template file
        val templateFile = fileStorageService.getFile(document.fileUrl)

        // Process template based on a document type
        val processedContent = when (document.docType) {
            DocumentType.WORD -> processWordTemplate(templateFile, fieldValues)
            else -> throw FileGenerationException("Template type ${document.docType} is not supported yet")
        }

        // Convert to the requested output format
        val outputContent = when (outputType) {
            DocumentType.WORD -> processedContent
            DocumentType.PDF -> convertWordToPdf(processedContent)
            DocumentType.PNG -> convertWordToImage(processedContent)
            else -> throw FileGenerationException("Output type $outputType is not supported")
        }

        // Save a generated file
        val extension = when (outputType) {
            DocumentType.WORD -> "docx"
            DocumentType.PDF -> "pdf"
            DocumentType.PNG -> "png"
            else -> "bin"
        }

        val fileName = "${document.name.replace(" ", "_")}.$extension"
        val savedPath = fileStorageService.saveGeneratedFile(outputContent, fileName, userId)

        return Pair(savedPath, outputContent)
    }

    /**
     * Get or generate document in specified format
     */
    fun getOrGenerateFormat(
        recordId: UUID,
        filePath: String,
        fieldValues: Map<String, String>,
        documentId: UUID,
        userId: UUID,
        requestedFormat: DocumentType
    ): ByteArray {
        
        val (_, content) = generateDocument(
            documentId = documentId,
            fieldValues = fieldValues,
            outputType = requestedFormat,
            userId = userId
        )
        return content
    }

    /**
     * Process Word template
     */
    private fun processWordTemplate(templateFile: File, fieldValues: Map<String, String>): ByteArray {
        try {
            FileInputStream(templateFile).use { fis ->
                val doc = XWPFDocument(fis)

                // Replace placeholders in paragraphs
                doc.paragraphs.forEach { paragraph ->
                    var text = paragraph.text
                    fieldValues.forEach { (key, value) ->
                        val placeholder = "\${$key}"
                        text = text.replace(placeholder, value)
                    }

                    // Handle date replacement
                    if (text.contains("\${date}")) {
                        text = text.replace("\${date}", dateFormatter.format(java.time.Instant.now()))
                    }

                    // Clear and set new text
                    if (text != paragraph.text) {
                        // Create a copy of runs to avoid ConcurrentModificationException when removing
                        val runsToProcess = paragraph.runs.toList()
                        for (i in runsToProcess.indices.reversed()) {
                            paragraph.removeRun(i)
                        }
                        val newRun = paragraph.createRun()
                        newRun.setText(text)
                    }
                }

                // Replace in tables
                doc.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { paragraph ->
                                var text = paragraph.text
                                fieldValues.forEach { (key, value) ->
                                    val placeholder = "\${$key}"
                                    text = text.replace(placeholder, value)
                                }

                                // Handle date replacement
                                if (text.contains("\${date}")) {
                                    text = text.replace("\${date}", dateFormatter.format(java.time.Instant.now()))
                                }

                                if (text != paragraph.text) {
                                    val runsToProcess = paragraph.runs.toList()
                                    for (i in runsToProcess.indices.reversed()) {
                                        paragraph.removeRun(i)
                                    }
                                    val newRun = paragraph.createRun()
                                    newRun.setText(text)
                                }
                            }
                        }
                    }
                }

                // Write to byte array
                val outputStream = ByteArrayOutputStream()
                doc.write(outputStream)
                doc.close()

                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            println("[DEBUG_LOG] processWordTemplate error: ${e.message}")
            e.printStackTrace()
            throw FileGenerationException("Failed to process Word template: ${e.message}")
        }
    }

    /**
     * Convert Word to PDF
     */
    private fun convertWordToPdf(wordContent: ByteArray): ByteArray {
        try {
            // Read Word document
            val doc = XWPFDocument(wordContent.inputStream())

            // Create PDF
            val outputStream = ByteArrayOutputStream()
            val pdfWriter = PdfWriter(outputStream)
            val pdfDoc = PdfDocument(pdfWriter)
            val pdfDocument = PdfLayoutDocument(pdfDoc)

            // Convert paragraphs to PDF
            doc.paragraphs.forEach { paragraph ->
                if (paragraph.text.isNotBlank()) {
                    pdfDocument.add(Paragraph(paragraph.text))
                }
            }

            // Convert tables to PDF (simplified)
            doc.tables.forEach { table ->
                table.rows.forEach { row ->
                    val rowText = row.tableCells.joinToString(" | ") { it.text }
                    if (rowText.isNotBlank()) {
                        pdfDocument.add(Paragraph(rowText))
                    }
                }
            }

            pdfDocument.close()
            doc.close()

            return outputStream.toByteArray()
        } catch (e: Exception) {
            throw FileGenerationException("Failed to convert to PDF: ${e.message}")
        }
    }

    /**
     * Convert Word to Image (PNG)
     */
    private fun convertWordToImage(wordContent: ByteArray): ByteArray {
        try {
            // Read Word document
            val doc = XWPFDocument(wordContent.inputStream())

            // Create png
            val width = 800
            val height = 1000
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics()

            // White background
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, width, height)

            // Draw text
            graphics.color = Color.BLACK
            graphics.font = Font("Arial", Font.PLAIN, 14)

            var y = 30
            doc.paragraphs.forEach { paragraph ->
                if (paragraph.text.isNotBlank()) {
                    graphics.drawString(paragraph.text, 20, y)
                    y += 25
                }
            }

            graphics.dispose()
            doc.close()

            // Convert to PNG bytes
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "PNG", outputStream)

            return outputStream.toByteArray()
        } catch (e: Exception) {
            throw FileGenerationException("Failed to convert to image: ${e.message}")
        }
    }
}
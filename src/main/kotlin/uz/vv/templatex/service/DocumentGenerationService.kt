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

    fun generateDocument(
        documentId: UUID,
        fieldValues: Map<String, String>,
        outputType: DocumentType,
        userId: UUID
    ): Pair<String, ByteArray> {

        val document = documentRepo.findByIdAndDeletedFalse(documentId)
            ?: throw DocumentNotFoundException("Document with id '$documentId' not found")

        val templateFile = fileStorageService.getFile(document.fileUrl)

        val processedContent = when (document.docType) {
            DocumentType.WORD -> processWordTemplate(templateFile, fieldValues)
            else -> throw FileGenerationException("Template type ${document.docType} is not supported yet")
        }

        val outputContent = convertToOutputFormat(processedContent, outputType)
        val fileName = "${document.name.replace(" ", "_")}.${getFileExtension(outputType)}"
        val savedPath = fileStorageService.saveGeneratedFile(outputContent, fileName, userId)

        return Pair(savedPath, outputContent)
    }

    private fun processWordTemplate(templateFile: File, fieldValues: Map<String, String>): ByteArray {
        try {
            FileInputStream(templateFile).use { fis ->
                val doc = XWPFDocument(fis)

                // 1. Paragraflardagi placeholderlarni almashtirish
                doc.paragraphs.forEach { replacePlaceholders(it, fieldValues) }

                // 2. Jadvallardagi placeholderlarni almashtirish
                doc.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { replacePlaceholders(it, fieldValues) }
                        }
                    }
                }

                val outputStream = ByteArrayOutputStream()
                doc.use { it.write(outputStream) }
                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            throw FileGenerationException("Failed to process Word template: ${e.message}")
        }
    }


    private fun replacePlaceholders(paragraph: org.apache.poi.xwpf.usermodel.XWPFParagraph, fieldValues: Map<String, String>) {
        var text = paragraph.text
        if (text.isNullOrBlank()) return

        fieldValues.forEach { (key, value) ->
            text = text.replace("\${$key}", value)
        }

        if (text.contains("\${date}")) {
            text = text.replace("\${date}", dateFormatter.format(java.time.Instant.now()))
        }

        if (text != paragraph.text) {
            for (i in paragraph.runs.size - 1 downTo 0) {
                paragraph.removeRun(i)
            }
            paragraph.createRun().setText(text)
        }
    }

    private fun convertToOutputFormat(content: ByteArray, type: DocumentType): ByteArray = when (type) {
        DocumentType.WORD -> content
        DocumentType.PDF -> convertWordToPdf(content)
        DocumentType.PNG -> convertWordToImage(content)
        else -> throw FileGenerationException("Output type $type is not supported")
    }

    private fun getFileExtension(type: DocumentType) = when (type) {
        DocumentType.WORD -> "docx"
        DocumentType.PDF -> "pdf"
        DocumentType.PNG -> "png"
        else -> "bin"
    }

    private fun convertWordToPdf(wordContent: ByteArray): ByteArray {
        try {
            val doc = XWPFDocument(wordContent.inputStream())
            val outputStream = ByteArrayOutputStream()
            val pdfDoc = PdfDocument(PdfWriter(outputStream))
            val pdfLayout = PdfLayoutDocument(pdfDoc)

            doc.paragraphs.filter { it.text.isNotBlank() }.forEach {
                pdfLayout.add(Paragraph(it.text))
            }

            doc.tables.forEach { table ->
                table.rows.forEach { row ->
                    val rowText = row.tableCells.joinToString(" | ") { it.text }
                    if (rowText.isNotBlank()) pdfLayout.add(Paragraph(rowText))
                }
            }

            pdfLayout.close()
            doc.close()
            return outputStream.toByteArray()
        } catch (e: Exception) {
            throw FileGenerationException("Failed to convert to PDF: ${e.message}")
        }
    }

    private fun convertWordToImage(wordContent: ByteArray): ByteArray {
        try {
            val doc = XWPFDocument(wordContent.inputStream())
            val image = BufferedImage(800, 1000, BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics().apply {
                color = Color.WHITE
                fillRect(0, 0, 800, 1000)
                color = Color.BLACK
                font = Font("Arial", Font.PLAIN, 14)
            }

            var y = 30
            doc.paragraphs.filter { it.text.isNotBlank() }.forEach {
                graphics.drawString(it.text, 20, y)
                y += 25
            }

            graphics.dispose()
            doc.close()

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "PNG", outputStream)
            return outputStream.toByteArray()
        } catch (e: Exception) {
            throw FileGenerationException("Failed to convert to image: ${e.message}")
        }
    }

    fun getOrGenerateFormat(
        recordId: UUID,
        filePath: String,
        fieldValues: Map<String, String>,
        documentId: UUID,
        userId: UUID,
        requestedFormat: DocumentType
    ): ByteArray {
        return generateDocument(documentId, fieldValues, requestedFormat, userId).second
    }
}
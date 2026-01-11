package uz.vv.templatex.entity

import jakarta.persistence.*
import uz.vv.templatex.base.BaseEntity
import uz.vv.templatex.enum.DocumentType

@Entity
@Table(name = "documents")
class Document(

    @Column(nullable = false)
    var orgName: String = "",

    @Column(name = "file_path", nullable = false)
    var filePath: String = "", // Word fayli saqlangan joy (S3 key yoki path)

    // Auto-generated keyName: 10_char_uuid + originalName
    @Column(nullable = false, unique = true, length = 100)
    var keyName: String = "",

    var version: Int = 1, // eski documentlar buzilmasligi uchun

    @Column(name = "size", nullable = false)
    var size: Long = 0,  // doc size

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var docType: DocumentType = DocumentType.WORD, // Word / PDF / PNG / etc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    var organization: Organization,

    ) : BaseEntity()
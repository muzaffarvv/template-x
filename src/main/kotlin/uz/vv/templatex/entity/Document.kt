package uz.vv.templatex.entity

import jakarta.persistence.*
import uz.vv.templatex.base.BaseEntity
import uz.vv.templatex.enum.DocumentType

@Entity
@Table(name = "documents")
class Document(

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "file_path", nullable = false)
    var filePath: String, // Word fayli saqlangan joy (S3 key yoki path)

    var version: Int = 1, // eski documentlar buzilmasligi uchun

    @Column(name = "size", nullable = false)
    var size: Long = 0,  // doc size

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var docType: DocumentType = DocumentType.WORD, // Word / PDF / Image / etc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    var organization: Organization,

    @OneToMany(mappedBy = "document", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false)
    var fields: MutableSet<DocumentField> = mutableSetOf()

) : BaseEntity()
package uz.vv.templatex.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import uz.vv.templatex.base.BaseEntity
import uz.vv.templatex.enum.DocumentType

@Entity
@Table(name = "record_classes")
class RecordDocument(

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(name = "generated_file_path", nullable = false, length = 500)
    var generatedFilePath: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "output_type", nullable = false, length = 20)
    var outputType: DocumentType,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    var fieldValues: Map<String, String>,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    var document: Document,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : BaseEntity()
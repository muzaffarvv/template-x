package uz.vv.templatex.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import uz.vv.templatex.base.BaseEntity
import uz.vv.templatex.enum.FieldType

@Entity
@Table(name = "document_fields")
class DocumentField(

    @Column(nullable = false, length = 72)
    var label: String, // UI'da ko'rinadigan nom: "Mijozning ismi"

    @Column(nullable = false, length = 60)
    var keyName: String, // placeholder: ${client_name} -> key: client_name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    var type: FieldType = FieldType.STRING,

    var isRequired: Boolean = false,

    var orderIndex: Int = 0, // fieldlar qaysi ketma-ketlikda chiqishini belgilaydi

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var constraints: Map<String, String>? = null, // JSONB rule: {"min": 18, "regex": "..."}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    var document: Document

) : BaseEntity()

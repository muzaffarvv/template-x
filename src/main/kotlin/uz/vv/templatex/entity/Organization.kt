package uz.vv.templatex.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import uz.vv.templatex.base.BaseEntity

@Entity
@Table(name = "organizations")
class Organization(

    @Column(nullable = false, unique = true, length = 30)
    var name: String,

    @Column(length = 100, nullable = false, unique = true)
    var address: String,

    @Column(length = 150)
    var tagline: String? = null, // about organization

    // Auto-generated code: ORG001, ORG002, ...
    @Column(nullable = false, unique = true, length = 20)
    var code: String = ""

) : BaseEntity()
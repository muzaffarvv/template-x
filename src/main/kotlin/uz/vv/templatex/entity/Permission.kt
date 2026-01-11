package uz.vv.templatex.entity

import jakarta.persistence.*
import uz.vv.templatex.base.BaseEntity

@Entity
@Table(name = "permissions")
data class Permission(

    @Column(nullable = false, unique = true, length = 75)
    var code: String,

    @Column(nullable = false, length = 75)
    var name: String

): BaseEntity()
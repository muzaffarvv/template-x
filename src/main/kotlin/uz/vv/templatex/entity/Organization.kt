package uz.vv.templatex.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import uz.vv.templatex.base.BaseEntity

@Entity
@Table(name = "organizations")
data class Organization(

    @Column(nullable = false, unique = true, length = 30)
    var name: String,

    @Column(length = 150)
    var tagline: String // about organization

) : BaseEntity()
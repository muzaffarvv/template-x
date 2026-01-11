package uz.vv.templatex.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import uz.vv.templatex.base.BaseEntity

@Entity
@Table(name = "users")
class User(

    @Column(length = 72)
    var firstName: String? = null,

    @Column(length = 60)
    var lastName: String? = null,

    @Column(nullable = false, unique = true, length = 32)
    var username: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @ManyToMany(fetch = FetchType.LAZY)
    var roles: MutableSet<Role> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    var organization: Organization

) : BaseEntity()
package uz.vv.templatex.base

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface BaseRepo<T : BaseEntity> : JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    fun findByIdAndDeletedFalse(id: UUID): T?

    fun trash(id: UUID): T?

    fun trashList(ids: List<UUID>): List<T>

    fun findAllNotDeleted(): List<T>

    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>

    fun saveAndRefresh(t: T): T
}
package uz.vv.templatex.base

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.util.*

class BaseRepoImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, UUID>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, UUID>(entityInformation, entityManager), BaseRepo<T> {

    private val isNotDeletedSpecification = Specification<T> { root, _, cb ->
        cb.equal(root.get<Boolean>("deleted"), false)
    }

    override fun findByIdAndDeletedFalse(id: UUID): T? =
        findOne(
            Specification.where(isNotDeletedSpecification)
                .and { root, _, cb -> cb.equal(root.get<UUID>("id"), id) }
        ).orElse(null)

    @Transactional
    override fun trash(id: UUID): T? = findById(id).orElse(null)?.apply {
        deleted = true
        save(this)
    }

    @Transactional
    override fun trashList(ids: List<UUID>): List<T> {
        val entities = findAllById(ids)
        entities.forEach { it.deleted = true }
        return saveAll(entities)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)

    override fun findAllNotDeletedForPageable(pageable: Pageable) =
        findAll(isNotDeletedSpecification, pageable)

    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }
}
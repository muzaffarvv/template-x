package uz.vv.templatex.base

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.util.*

abstract class BaseServiceImpl<
        E : BaseEntity,
        CreateDTO,
        UpdateDTO,
        ResponseDTO : BaseDTO,
        Mapper : BaseMapper<E, ResponseDTO>,
        Repo : BaseRepo<E>
        >(
    protected open val repository: Repo,
    protected open val mapper: Mapper
) : BaseService<CreateDTO, UpdateDTO, ResponseDTO> {

    /* ===================== ABSTRACT ===================== */

    protected abstract fun convertCreateDtoToEntity(dto: CreateDTO): E

    protected abstract fun updateEntityFromDto(entity: E, dto: UpdateDTO)

    /* ===================== VALIDATION HOOKS ===================== */

    protected open fun validateCreate(dto: CreateDTO) {}

    protected open fun validateUpdate(id: UUID, dto: UpdateDTO) {}

    protected open fun validateDelete(id: UUID) {}

    /* ===================== HELPER ===================== */

    protected fun getEntityOrNull(id: UUID): E? =
        repository.findByIdAndDeletedFalse(id)

    protected fun saveAndRefresh(entity: E): E =
        repository.saveAndRefresh(entity)

    /* ===================== CRUD ===================== */

    @Transactional
    override fun create(dto: CreateDTO): ResponseDTO {
        validateCreate(dto)
        val entity = convertCreateDtoToEntity(dto)
        val saved = repository.saveAndRefresh(entity)
        return mapper.toDTO(saved)
    }

    @Transactional(readOnly = true)
    override fun getById(id: UUID): ResponseDTO {
        throw UnsupportedOperationException(
            "getById must be overridden in EntityService"
        )
    }

    @Transactional
    override fun update(id: UUID, dto: UpdateDTO): ResponseDTO {
        throw UnsupportedOperationException(
            "update must be overridden in EntityService"
        )
    }

    @Transactional
    override fun delete(id: UUID) {
        validateDelete(id)
        repository.trash(id)
    }

    @Transactional
    override fun deleteAll(ids: List<UUID>) {
        repository.trashList(ids)
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<ResponseDTO> =
        mapper.toDTOList(repository.findAllNotDeleted())

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): Page<ResponseDTO> =
        repository.findAllNotDeletedForPageable(pageable)
            .map(mapper::toDTO)
}
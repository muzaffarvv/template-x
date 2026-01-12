package uz.vv.templatex.base

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface BaseService<CreateDTO, UpdateDTO, ResponseDTO> {

    fun create(dto: CreateDTO): ResponseDTO

    fun getById(id: UUID): ResponseDTO

    fun update(id: UUID, dto: UpdateDTO): ResponseDTO

    fun delete(id: UUID)

    fun deleteAll(ids: List<UUID>)

    fun getAll(): List<ResponseDTO>

    fun getAll(pageable: Pageable): Page<ResponseDTO>
}
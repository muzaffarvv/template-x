package uz.vv.templatex.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import uz.vv.templatex.dto.OrganizationCreateDTO
import uz.vv.templatex.dto.OrganizationDTO
import uz.vv.templatex.dto.OrganizationUpdateDTO
import uz.vv.templatex.service.OrganizationService
import java.util.*

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @PostMapping
    fun create(@RequestBody dto: OrganizationCreateDTO): OrganizationDTO =
        organizationService.create(dto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): OrganizationDTO =
        organizationService.getById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody dto: OrganizationUpdateDTO): OrganizationDTO =
        organizationService.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        organizationService.delete(id)

    @GetMapping
    fun getAll(): List<OrganizationDTO> =
        organizationService.getAll()

    @GetMapping("/page")
    fun getAllPageable(pageable: Pageable): Page<OrganizationDTO> =
        organizationService.getAll(pageable)
}

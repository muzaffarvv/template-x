package uz.vv.templatex

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import uz.vv.templatex.base.BaseRepoImpl

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = ["uz.vv.templatex"],
    repositoryBaseClass = BaseRepoImpl::class
)
class TemplateXApplication

fun main(args: Array<String>) {
    runApplication<TemplateXApplication>(*args)
}


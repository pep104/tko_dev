package ru.sarmatin.template.data.repository.example

import ru.sarmatin.template.core.exception.Failure
import ru.sarmatin.template.core.functional.Either
import ru.sarmatin.template.data.framework.source.example.ExampleSource
import ru.sarmatin.template.data.repository.BaseRepository
import ru.sarmatin.template.domain.model.ExampleModel
import javax.inject.Inject

class ExampleRepositoryImpl @Inject constructor(private val exampleSource: ExampleSource) : ExampleRepository, BaseRepository() {
    override suspend fun getExample(): Either<Failure, ExampleModel> = request(exampleSource.example()) { it.toModel() }
}
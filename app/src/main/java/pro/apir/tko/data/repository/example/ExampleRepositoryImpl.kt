package pro.apir.tko.data.repository.example

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.source.example.ExampleSource
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.ExampleModel
import javax.inject.Inject

class ExampleRepositoryImpl @Inject constructor(private val exampleSource: ExampleSource) : ExampleRepository, BaseRepository() {
    override suspend fun getExample(): Either<Failure, ExampleModel> = request(exampleSource.example()) { it.toModel() }
}
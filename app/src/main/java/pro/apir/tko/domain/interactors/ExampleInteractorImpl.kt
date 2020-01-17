package pro.apir.tko.domain.interactors

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.example.ExampleRepository
import pro.apir.tko.domain.model.ExampleModel
import javax.inject.Inject

class ExampleInteractorImpl @Inject constructor(private val exampleRepository: ExampleRepository) : ExampleInteractor {
    override suspend fun getExample(): Either<Failure, ExampleModel> = exampleRepository.getExample()
}
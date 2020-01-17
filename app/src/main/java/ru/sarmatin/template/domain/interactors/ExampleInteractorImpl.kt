package ru.sarmatin.template.domain.interactors

import ru.sarmatin.template.core.exception.Failure
import ru.sarmatin.template.core.functional.Either
import ru.sarmatin.template.data.repository.example.ExampleRepository
import ru.sarmatin.template.domain.model.ExampleModel
import javax.inject.Inject

class ExampleInteractorImpl @Inject constructor(private val exampleRepository: ExampleRepository) : ExampleInteractor {
    override suspend fun getExample(): Either<Failure, ExampleModel> = exampleRepository.getExample()
}
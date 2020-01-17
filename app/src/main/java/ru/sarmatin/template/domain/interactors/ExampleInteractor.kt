package ru.sarmatin.template.domain.interactors

import ru.sarmatin.template.core.exception.Failure
import ru.sarmatin.template.core.functional.Either
import ru.sarmatin.template.domain.model.ExampleModel

interface ExampleInteractor {

    suspend fun getExample(): Either<Failure, ExampleModel>

}
package pro.apir.tko.domain.interactors

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ExampleModel

interface ExampleInteractor {

    suspend fun getExample(): Either<Failure, ExampleModel>

}
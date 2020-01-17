package pro.apir.tko.data.repository.example

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ExampleModel

interface ExampleRepository {

   suspend fun getExample() : Either<Failure, ExampleModel>

}
package ru.sarmatin.template.data.repository.example

import ru.sarmatin.template.core.exception.Failure
import ru.sarmatin.template.core.functional.Either
import ru.sarmatin.template.domain.model.ExampleModel

interface ExampleRepository {

   suspend fun getExample() : Either<Failure, ExampleModel>

}
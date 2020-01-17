package ru.sarmatin.template.data.framework.network.model

import ru.sarmatin.template.domain.model.ExampleModel

data class ExampleResponse(val field: String): BaseResponse(){
    fun toModel() = ExampleModel(field)
}
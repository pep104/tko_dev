package pro.apir.tko.data.framework.network.model

import pro.apir.tko.domain.model.ExampleModel

data class ExampleResponse(val field: String): BaseResponse(){
    fun toModel() = ExampleModel(field)
}
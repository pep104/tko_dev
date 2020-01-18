package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.ExampleResponse
import retrofit2.Response


//Create new interface for each entity
interface MainApi {


    suspend fun example(): Response<ExampleResponse>

}
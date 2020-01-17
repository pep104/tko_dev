package pro.apir.tko.data.framework.network.api

import retrofit2.Response
import pro.apir.tko.data.framework.network.model.ExampleResponse


//Create new interface for each entity
interface ExampleApi {


    suspend fun example(): Response<ExampleResponse>

}
package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.response.ContainersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


//Create new interface for each entityя
interface InventoryApi {

    //TEMP
    @GET("waste-area/container/")
    suspend fun getContainers(@Query("page") page: Int, @Query("page_size") pageSize: Int, @Query("location") location: String): Response<ContainersResponse>

}
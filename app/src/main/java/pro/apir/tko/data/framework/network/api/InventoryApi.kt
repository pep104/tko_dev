package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.response.ContainerAreaDetailedResponse
import pro.apir.tko.data.framework.network.model.response.ContainerAreasResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface InventoryApi {

    //TEMP
    @GET("waste-area/container/")
    suspend fun getContainerAreas(@Query("page") page: Int, @Query("page_size") pageSize: Int, @Query("location") location: String): Response<ContainerAreasResponse>

    @GET("waste-area/container/{id}/")
    suspend fun getContainerArea(@Path("id") id: Long): Response<ContainerAreaDetailedResponse>

//    @GET("public/map/list/")
//    suspend fun getContainerAreasByBoundingBox(@Query("lng_min") lngMin: String, @Query("lat_min") latMin: String, @Query("lng_max") lngMax: String, @Query("lat_max") latMax: String): Response<>


}
package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.response.ContainerAreaListResponse
import pro.apir.tko.data.framework.network.model.response.ContainerAreaResponse
import retrofit2.Response
import retrofit2.http.*


interface InventoryApi {

    //TEMP
    @GET("waste-area/container/")
    suspend fun getContainerAreas(@Query("page") page: Int, @Query("page_size") pageSize: Int, @Query("location") location: String): Response<ContainerAreaListResponse>

    @GET("waste-area/container/{id}/")
    suspend fun getContainerArea(@Path("id") id: Long): Response<ContainerAreaResponse>

    @PATCH("waste-area/container/{id}/")
    suspend fun updateContainerArea(@Path("id") id: Long, @Body containerArea: ContainerAreaDetailedRequest): Response<ContainerAreaResponse>

    @POST("waste-area/container/")
    suspend fun createContainerArea(@Body containerArea: ContainerAreaDetailedRequest): Response<ContainerAreaResponse>

//    @GET("public/map/list/")
//    suspend fun getContainerAreasByBoundingBox(@Query("lng_min") lngMin: String, @Query("lat_min") latMin: String, @Query("lng_max") lngMax: String, @Query("lat_max") latMax: String): Response<>

}
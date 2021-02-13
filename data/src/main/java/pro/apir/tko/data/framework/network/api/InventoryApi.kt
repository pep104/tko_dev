package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.calladapter.ApiResult
import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.response.ContainerAreaListResponse
import pro.apir.tko.data.framework.network.model.response.ContainerAreaResponse
import retrofit2.http.*


interface InventoryApi {

    //TEMP
    @GET("waste-area/container/")
    suspend fun getContainerAreas(@Query("page") page: Int, @Query("page_size") pageSize: Int, @Query("location") location: String): ApiResult<ContainerAreaListResponse>

    @GET("waste-area/container/{id}/")
    suspend fun getContainerArea(@Path("id") id: Long): ApiResult<ContainerAreaResponse>

    @PATCH("waste-area/container/{id}/")
    suspend fun updateContainerArea(@Path("id") id: Long, @Body containerArea: ContainerAreaDetailedRequest): ApiResult<ContainerAreaResponse>

    @POST("waste-area/container/")
    suspend fun createContainerArea(@Body containerArea: ContainerAreaDetailedRequest): ApiResult<ContainerAreaResponse>

    @GET("waste-area/")
    suspend fun getContainerAreasByBoundingBox(@Query("lng_min") lngMin: String, @Query("lat_min") latMin: String, @Query("lng_max") lngMax: String, @Query("lat_max") latMax: String, @Query("page") page: Int, @Query("page_size") pageSize: Int): ApiResult<ContainerAreaListResponse>

    @GET("waste-area/")
    suspend fun searchContainer(@Query("search") search: String): ApiResult<ContainerAreaListResponse>

}
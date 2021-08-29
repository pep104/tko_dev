package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.calladapter.ApiResult
import pro.apir.tko.data.framework.network.model.response.MapPointsResponse
import pro.apir.tko.domain.model.map.MapPointType
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
interface MapApi {

    @GET("public/map/list/")
    suspend fun getPoints(
        @Query("lng_min") lngMin: String,
        @Query("lat_min") latMin: String,
        @Query("lng_max") lngMax: String,
        @Query("lat_max") latMax: String,
        @Query("type") type: MapPointType = MapPointType.CONTAINER_WASTE_AREA
    ): ApiResult<MapPointsResponse>

}
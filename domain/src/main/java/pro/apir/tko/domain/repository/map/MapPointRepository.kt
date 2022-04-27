package pro.apir.tko.domain.repository.map

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.map.MapPointModel

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
interface MapPointRepository {

    fun getMapPoints(): Flow<List<MapPointModel>>

    suspend fun fetch(bbox: BBoxModel)

}
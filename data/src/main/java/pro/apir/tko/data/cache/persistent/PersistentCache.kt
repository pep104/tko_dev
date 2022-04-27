package pro.apir.tko.data.cache.persistent

import kotlinx.coroutines.flow.flow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.data.framework.network.calladapter.ApiResult

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */

sealed class CacheStrategy {

    object GetAndUpdate : CacheStrategy()

    object UpdateAndGet : CacheStrategy()

}


inline fun <N, R> fetch(
    strategy: CacheStrategy = CacheStrategy.GetAndUpdate,
    crossinline fetchFromCache: () -> R? = { null },
    crossinline shouldFetchFromNetwork: (R?) -> Boolean = { true },
    crossinline fetchFromNetwork: suspend () -> ApiResult<N>,
    crossinline processNetworkResult: (apiResult: ApiResult<N>) -> Unit = { Unit },
    crossinline saveNetworkResult: (N) -> Unit = { Unit },
    crossinline onFetchFailed: (Failure) -> Unit = { Unit },
) = flow {
    var isEmitted = false
    val cacheData = fetchFromCache()

    if (strategy is CacheStrategy.GetAndUpdate)
        cacheData?.let {
            emit(it)
            isEmitted = true
        }

    if (shouldFetchFromNetwork(cacheData)) {
        val apiResult = fetchFromNetwork()
        processNetworkResult(apiResult)
        when (apiResult) {
            is ApiResult.Success -> {
                //Сохраняем
                saveNetworkResult(apiResult.data)
                //Отдаем сохраненные данные
                fetchFromCache()?.let {
                    emit(Resource.Success(it))
                }
            }
            is ApiResult.Error -> {
                onFetchFailed(apiResult.failure)
                emit(Resource.Error(apiResult.failure))
            }
        }
    } else if (!isEmitted) {
        cacheData?.let {
            emit(it)
        }
    }
}
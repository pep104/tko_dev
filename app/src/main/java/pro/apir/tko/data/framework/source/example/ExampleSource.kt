package pro.apir.tko.data.framework.source.example

import retrofit2.Retrofit
import pro.apir.tko.data.framework.network.api.MainApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleSource @Inject constructor(private val retrofit: Retrofit) : MainApi {

    private val api by lazy { retrofit.create(MainApi::class.java) }

    override suspend fun example()  = api.example()

}
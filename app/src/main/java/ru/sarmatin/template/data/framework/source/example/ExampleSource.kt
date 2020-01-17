package ru.sarmatin.template.data.framework.source.example

import retrofit2.Response
import retrofit2.Retrofit
import ru.sarmatin.template.data.framework.network.api.ExampleApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleSource @Inject constructor(private val retrofit: Retrofit) : ExampleApi {

    private val api by lazy { retrofit.create(ExampleApi::class.java) }

    override suspend fun example()  = api.example()

}
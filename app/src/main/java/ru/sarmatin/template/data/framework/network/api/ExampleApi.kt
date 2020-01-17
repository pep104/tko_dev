package ru.sarmatin.template.data.framework.network.api

import retrofit2.Response
import ru.sarmatin.template.data.framework.network.model.ExampleResponse

/**
 * Created by antonsarmatin
 * Date: 2019-12-17
 * Project: android-template
 */

//Create new interface for each entity
interface ExampleApi {


    suspend fun example(): Response<ExampleResponse>

}
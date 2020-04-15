package pro.apir.tko.data.framework.network.model.request

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
data class SuggestionRequest(val query: String, val count: Int? = null)
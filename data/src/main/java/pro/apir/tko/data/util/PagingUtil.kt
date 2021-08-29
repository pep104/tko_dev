package pro.apir.tko.data.util

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
suspend inline fun <T> fetchPages(
    initialPageRequest: () -> T,
    pageRequest: (String) -> T,
    processPage: (T) -> String?,
) {
    var nextPageLink: String?
    nextPageLink = processPage(initialPageRequest())
    while (!nextPageLink.isNullOrBlank()) {
        nextPageLink = processPage(pageRequest(nextPageLink))
    }
}
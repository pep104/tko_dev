package pro.apir.tko.presentation.dict

import pro.apir.tko.core.types.Dictionary

/**
 * Created by antonsarmatin
 * Date: 2020-01-31
 * Project: tko-android
 */
interface OptionsDictionariesManager {

    fun getAccessOptionDictionary(): Dictionary

    fun getBasementOptionsDictionary(): Dictionary

    fun getFenceOptionsDictionary(): Dictionary

    fun getRemotenessOptionsDictionary(): Dictionary

    fun getCoverageOptionsDictionary(): Dictionary

    fun getKGOOptionsDictionary(): Dictionary

}
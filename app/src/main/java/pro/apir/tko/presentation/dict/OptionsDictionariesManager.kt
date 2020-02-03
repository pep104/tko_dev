package pro.apir.tko.presentation.dict

import pro.apir.tko.core.types.Dictionary

/**
 * Created by antonsarmatin
 * Date: 2020-01-31
 * Project: tko-android
 */
interface OptionsDictionariesManager {

    fun getAccessOptionDictionary(): Dictionary

    fun getFenceOptionsDictionary(): Dictionary

    fun getCoverageOptionsDictionary(): Dictionary

    fun getKGOOptionsDictionary(): Dictionary

    fun getHasCoverOptionsDictionary(): Dictionary

    fun getInfoPlateDictionary(): Dictionary

    fun getContainerTypeDictionary(): Dictionary

}
package pro.apir.tko.data.framework.dict

import android.content.Context
import pro.apir.tko.core.types.Dictionary
import pro.apir.tko.core.types.dictOf
import javax.inject.Inject

//Strings to resources
class OptionsDictionariesManagerImpl @Inject constructor(private val context: Context) : OptionsDictionariesManager {

    private val accessOptions by lazy {
        dictOf(
                "FREE" to "Подъездные пути свободны",
                "NOT_FREE" to "Подъездные пути не свободны"
        )
    }

    private val fenceOptions by lazy {
        dictOf(
                "CORRESPONDS" to "Соответствует",
                "NOT_CORRESPONDS" to "Не соответствуе",
                "NEEDS_REPAIR" to "Требует ремонта"
        )
    }

    private val coverageOptions by lazy {
        dictOf(
                "ASPHALT" to "Асфальт",
                "CONCRETE" to "Бетон",
                "SOIL" to "Грунт"
        )
    }

    private val kgoOptions by lazy {
        dictOf(
                "PRESENT" to "Есть",
                "ABSENT" to "Нет",
                "NOT_PROVIDED" to "Не предусмотрена"
        )
    }

    private val trueFalseOptions by lazy {
        dictOf(
                "TRUE" to "Есть",
                "FALSE" to "Нет"
        )
    }

    private val containerTypeOptions by lazy {
        dictOf(
                "STANDARD" to "Стандартный",
                "EURO" to "Евро",
                "SEPARATE" to "Разд. сбор",
                "BUNKER" to "Бункер"
        )
    }

    private val periodicityOptions by lazy {
        dictOf(
                "DAILY" to "Ежедневно",
                "EVERY_OTHER_DAY" to "Через день",
                "PER_THREE_DAYS" to "Раз в три дня",
                "ON_WEEKDAYS" to "Дни недели",
                "ON_DATES" to "Даты",
                "BY_REQUEST" to "По заявке"
        )
    }

    override fun getAccessOptionDictionary(): Dictionary = accessOptions
    override fun getFenceOptionsDictionary(): Dictionary = fenceOptions
    override fun getCoverageOptionsDictionary(): Dictionary = coverageOptions
    override fun getKGOOptionsDictionary(): Dictionary = kgoOptions
    override fun getHasCoverOptionsDictionary(): Dictionary = trueFalseOptions
    override fun getInfoPlateDictionary(): Dictionary = trueFalseOptions
    override fun getContainerTypeDictionary(): Dictionary = containerTypeOptions
    override fun getPeriodicityDictionary(): Dictionary = periodicityOptions
}
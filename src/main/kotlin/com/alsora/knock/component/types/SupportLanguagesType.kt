package com.alsora.knock.component.types

import java.util.Locale

enum class SupportLanguagesType(val isoCode: String, val bcp47: String, val locale: Locale) {
    KR("KR", "ko-KR", Locale.KOREAN),
    US("US", "en-US", Locale.ENGLISH),
    CN("CN", "zh-CN", Locale.CHINESE);

    companion object {
        fun fromIsoCode(isoCode: String?): SupportLanguagesType? {
            isoCode?.let {
                return entries.find { it.isoCode == isoCode }
            }
            return null
        }
        fun fromBcp47(bcp47: String?): SupportLanguagesType? {
            bcp47?.let {
                return entries.find { it.bcp47 == bcp47 }
            }
            return null
        }
    }
}
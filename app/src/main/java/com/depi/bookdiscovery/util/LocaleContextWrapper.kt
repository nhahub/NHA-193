package com.depi.bookdiscovery.util

import android.content.Context
import android.content.ContextWrapper
import java.util.Locale

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var context = context
            val res = context.resources
            val configuration = res.configuration

            configuration.setLocale(newLocale)
            val localeList = android.os.LocaleList(newLocale)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
            return LocaleContextWrapper(context)
        }
    }
}

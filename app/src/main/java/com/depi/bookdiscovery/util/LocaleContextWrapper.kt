package com.depi.bookdiscovery.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var context = context
            val res = context.resources
            val configuration = res.configuration

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(newLocale)
                val localeList = android.os.LocaleList(newLocale)
                configuration.setLocales(localeList)
                context = context.createConfigurationContext(configuration)
            } else {
                configuration.setLocale(newLocale)
                res.updateConfiguration(configuration, res.displayMetrics)
            }
            return LocaleContextWrapper(context)
        }
    }
}

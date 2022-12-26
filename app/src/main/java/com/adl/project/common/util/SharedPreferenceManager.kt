package com.adl.project.common.util
import android.content.Context
import android.content.SharedPreferences

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: SharedPreference를 편하게 사용하기 위한 Manager 클래스
 */

class SharedPreferenceManager {

    companion object {
        private var sp: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        private fun getInstance(context: Context): SharedPreferences {
            synchronized(this) {
                sp = context.getSharedPreferences("DBG::SharedPreferencesFile", Context.MODE_PRIVATE)
                return sp!!
            }
        }

        fun putLong(context: Context, key: String, value: Long) {
            editor = getInstance(context).edit()
            editor!!.putLong(key, value)
            editor!!.apply()
        }

        fun getLong(context: Context, key: String, default: Long): Long {
            return getInstance(context).getLong(key, default)
        }

        fun putString(context: Context, key: String, value: String) {
            editor = getInstance(context).edit()
            editor!!.putString(key, value)
            editor!!.apply()
        }

        fun getString(context: Context, key: String, default: String): String {
            return getInstance(context).getString(key, default)!!
        }

        fun putInt(context: Context, key: String, value: Int) {
            editor = getInstance(context).edit()
            editor!!.putInt(key, value)
            editor!!.apply()
        }

        fun getInt(context: Context, key: String, default: Int): Int {
            return getInstance(context).getInt(key, default)
        }
    }

}
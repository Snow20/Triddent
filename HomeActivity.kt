package com.example.triddent

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

enum class ProviderType{
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setup
        val bundle : Bundle? = intent.extras
        val email : String? = bundle?.getString(key:"email")
        val provider : String? = bundle?.getString(key:"provider")
        setup(email: email?:"",provider:provider?:"")

        //guardado de datos
        val prefs:SharedPreferences.Editor! = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider",provider)
        prefs.apply()

        //Borrado de datos
        val prefs:SharedPreferences.Editor! = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

    }
}
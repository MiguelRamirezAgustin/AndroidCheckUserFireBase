package com.ejemplo.projectuserfirebaseemplyes.Utilities

import android.util.Patterns

class Utils {
    companion object{
        fun validateEmal(email:String):Boolean{
            return  Patterns.EMAIL_ADDRESS.toRegex().matches(email)
        }
    }
}
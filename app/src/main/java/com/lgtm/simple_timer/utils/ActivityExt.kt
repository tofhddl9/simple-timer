package com.lgtm.simple_timer.utils

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackBar(msg:String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this.findViewById(android.R.id.content), msg, duration).show()
}
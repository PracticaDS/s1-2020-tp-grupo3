package ar.edu.unq.pdes.myprivateblog.utils

import android.content.Context
import android.widget.Toast

fun Context.longToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

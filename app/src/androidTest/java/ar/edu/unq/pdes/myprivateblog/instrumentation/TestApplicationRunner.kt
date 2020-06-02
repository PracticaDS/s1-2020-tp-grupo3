package ar.edu.unq.pdes.myprivateblog.instrumentation

import androidx.test.runner.AndroidJUnitRunner

import android.app.Instrumentation
import android.content.Context

class TestApplicationRunner: AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?)
            = Instrumentation.newApplication(TestApplication::class.java, context)
}
package ar.edu.unq.pdes.myprivateblog.data

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


interface EventTracker  {
    fun logEvent(nameOfEvent : String) {}
}

class GoogleAnalytics(firebaseAnalytics: FirebaseAnalytics) : EventTracker{

    var analytics: FirebaseAnalytics

    init {
        this.analytics = firebaseAnalytics
    }

    override fun logEvent(nameOfEvent: String) {
        val params = Bundle()
        analytics.logEvent(nameOfEvent, params)
    }
}
package ar.edu.unq.pdes.myprivateblog.data

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


interface TrackEvents  {
    fun trackEvent() {}
}

class GoogleAnalytics(firebaseAnalytics: FirebaseAnalytics, nameOfEvent : String) : TrackEvents{

    var analytics: FirebaseAnalytics
    var nameOfEvent : String

    init {
        this.analytics = firebaseAnalytics
        this.nameOfEvent = nameOfEvent
    }

    override fun trackEvent() {
        val params = Bundle()
        analytics.logEvent(nameOfEvent, params)
    }
}
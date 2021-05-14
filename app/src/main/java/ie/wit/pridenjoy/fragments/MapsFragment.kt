package ie.wit.pridenjoy.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import ie.wit.pridenjoy.helpers.getAllMyEvents
import ie.wit.pridenjoy.helpers.setMapMarker
import ie.wit.pridenjoy.helpers.trackLocation

import ie.wit.pridenjoy.main.MainApp

class MapsFragment : SupportMapFragment(), OnMapReadyCallback{

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        getMapAsync(this)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        app.mMap = googleMap
        app.mMap.isMyLocationEnabled = true
        app.mMap.uiSettings.isZoomControlsEnabled = true
        app.mMap.uiSettings.setAllGesturesEnabled(true)
        app.mMap.clear()
        trackLocation(app)
        setMapMarker(app)
        getAllMyEvents(app)
    }
}
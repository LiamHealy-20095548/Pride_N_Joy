package ie.wit.pridenjoy.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.Location
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_placemark_maps.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_edit.view.*


class MapPlacemarkActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private val LOCATION_REQUEST_CODE = 101
    private lateinit var mMap: GoogleMap
    private lateinit var app: MainApp
    private lateinit var map: GoogleMap
    var location = Location()

    var toastFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setTheme(R.style.AppTheme)

        location = intent.extras?.getParcelable<Location>("location")!!
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val toolbarLayout = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val animationDrawable = toolbarLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(8000)
        animationDrawable.setExitFadeDuration(8000)
        animationDrawable.start()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if (!toastFlag) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Setting Event Location")
            builder.setMessage("Drag and Drop the marker to set the Event Location")

            builder.setPositiveButton("OK, GOT IT!") { dialog, which ->
                Toast.makeText(applicationContext,"OK, GOT IT!", Toast.LENGTH_SHORT).show()
                toastFlag = true
            }

            builder.setNeutralButton("RETURN") { dialog, which ->
                Toast.makeText(applicationContext,"Set Location Cancelled", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            builder.show()
        }

        map = googleMap
        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)

        val mapSettings = map.uiSettings

        val loc = LatLng(location.lat, location.lng)
        val options = MarkerOptions()
                .title("Event Location")
                .snippet("GPS : " + loc.toString())
                .draggable(true)
                .position(loc)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pindrop_rainbow_map_icon_48))
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        map.getUiSettings().setZoomControlsEnabled(true)

        map.getUiSettings().setAllGesturesEnabled(true)
        mapSettings?.isZoomGesturesEnabled = true
        mapSettings?.isScrollGesturesEnabled = true
        mapSettings?.isTiltGesturesEnabled = true
        mapSettings?.isRotateGesturesEnabled = true
        mapSettings?.isCompassEnabled = true
        mapSettings?.isMapToolbarEnabled = true

        val cameraPosition = CameraPosition.Builder()
            .target(loc)
            .zoom(14.0f)
            .bearing(0f)
            .tilt(0f)
            .build()

        map?.animateCamera(CameraUpdateFactory.newCameraPosition(
            cameraPosition))

        mapSearch.setOnClickListener {val intent = Intent(this@MapPlacemarkActivity, ComingSoonActivity::class.java)
            intent.putExtra("key", "Place")
            startActivity(intent) }

        fun requestPermission(permissionType: String, requestCode: Int) {

            ActivityCompat.requestPermissions(this,
                arrayOf(permissionType), requestCode)
        }

            mMap = googleMap

            if (mMap != null) {
                val permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)

                if (permission == PackageManager.PERMISSION_GRANTED) {
                    mMap?.isMyLocationEnabled = true
                } else {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_REQUEST_CODE)
                }
            }
        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                        "Unable to show users Current Location - Permission Required",
                        Toast.LENGTH_LONG).show()
                } else {
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    Toast.makeText(this,"Drag the marker to set the Location", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom

   }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val loc = LatLng(location.lat, location.lng)
        marker.setSnippet("GPS : " + loc.toString())

        return false
    }

}
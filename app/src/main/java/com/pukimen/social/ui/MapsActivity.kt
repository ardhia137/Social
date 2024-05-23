package com.pukimen.social.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.pukimen.social.R
import com.pukimen.social.data.model.StoryModel
import com.pukimen.social.databinding.ActivityMapsBinding
import com.pukimen.social.ui.auth.AuthViewModel
import com.pukimen.social.ui.story.StoryViewModel
import com.pukimen.social.utils.Results

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var newsData: List<StoryModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: StoryViewModel by viewModels { factory }
        val authViewModel: AuthViewModel by viewModels { factory }

        authViewModel.getSession().observe(this) { user ->
            val token = user.token

            viewModel.getLocationStory(token!!, 1).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Results.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Results.Success -> {
                            binding.progressBar.visibility = View.GONE
                            newsData = result.data
                            Log.e(TAG, "News data loaded: $newsData")
                            addManyMarker() 
                        }
                        is Results.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Terjadi kesalahan: ${result.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()
        if (newsData.isNotEmpty()) {
            addManyMarker()
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker() {
        if (this::mMap.isInitialized && newsData.isNotEmpty()) {
            newsData.forEach { data ->
                val lat = data.lat?.toDoubleOrNull()
                val lon = data.lon?.toDoubleOrNull()
                if (lat != null && lon != null) {
                    val latLng = LatLng(lat, lon)
                    mMap.addMarker(MarkerOptions().position(latLng).title(data.name))
                    boundsBuilder.include(latLng)
                } else {
                    Log.e(TAG, "Invalid coordinates for tourism: $data")
                }
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
            // Optionally, move the camera to the first marker

        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

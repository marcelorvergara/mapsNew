package android.example.mapsNew

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    lateinit var context: Context
    lateinit var mMap: GoogleMap

    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        context = this@MapsActivity
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        HitApi(this@MapsActivity,-22.82 ,-43.16,10000,"Hospitality").execute()
    }

    private inner class HitApi: AsyncTask<Void, Void, String>{
        var context : Context? = null
        var lat : Double? = null
        var lng : Double? = null
        var radius : Int? = null
        var type : String? = null

        constructor(context: Context,lat: Double,lng: Double,radius: Int,type: String) {
            this.context = context
            this.lat = lat
            this.lng = lng
            this.radius = radius
            this.type = type
        }


        override fun doInBackground(vararg params: Void?): String {
            return GooglePlacesApi().getPlacesJson(context as Context,lat as  Double,lng as Double,radius as Int,type as String)

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val gson = GsonBuilder().create()
            val root = gson.fromJson(result, PlacesRootClass::class.java)
            addMarkers(root)
        }
    }

    public fun addMarkers(root: PlacesRootClass){
        for (result  in root.results){
            val p  = LatLng(result.geometry.location.lat, result.geometry.location.lng)
            mMap!!.addMarker(MarkerOptions().position(p).title(result.name))
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-22.82 ,-43.16)))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }
}

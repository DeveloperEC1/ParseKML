package eliorcohen.com.parsekml;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlLineString;
import com.google.maps.android.data.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private ArrayList<LatLng> pathPoints, coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void initUI() {
        mMapView = findViewById(R.id.map);

        pathPoints = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.425828, 78.459774), 17));

        InputStream kmlInputStream = getResources().openRawResource(R.raw.my_kml);
        try {
            KmlLayer kmlLayer = new KmlLayer(mGoogleMap, kmlInputStream, this);
            kmlLayer.addLayerToMap();
            if (kmlLayer.getContainers() != null) {
                for (KmlContainer container : kmlLayer.getContainers()) {
                    if (container.hasPlacemarks()) {
                        for (KmlPlacemark placemark : container.getPlacemarks()) {
                            Geometry geometry = placemark.getGeometry();
                            if (geometry.getGeometryType().equals("LineString")) {
                                KmlLineString kmlLineString = (KmlLineString) geometry;
                                coords = kmlLineString.getGeometryObject();
                                pathPoints.addAll(coords);
                            }
                            if (placemark.hasProperty("name")) {
                                Log.d("Name", "" + placemark.getProperty("name"));
                            }
                        }
                    }
                }
                for (LatLng latLng : pathPoints) {
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

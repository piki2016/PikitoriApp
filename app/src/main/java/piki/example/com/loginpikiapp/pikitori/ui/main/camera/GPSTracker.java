package piki.example.com.loginpikiapp.pikitori.ui.main.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                    // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        // 사용자가 임의로 권한을 취소 시킨경우, 권한 재요청
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                100);
                    } else {
                        //최초로 권한을 요청하는경우 (첫실행)
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                100);
                    }

                }
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
    
    
    /**
     * Function to check if best network provider
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // Setting Icon to Dialog
//        //alertDialog.setIcon(R.drawable.delete);
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            dialog.cancel();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();

        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("위치 서비스 설정");
        gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
        gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // GPS설정 화면으로 이동
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create().show();
    }
    
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    public String convertTagGPSFormat(double coordinate){
        String strlatitude = Location.convert(coordinate,Location.FORMAT_SECONDS);
        String[] arrlatitude = strlatitude.split(":");
        StringBuilder sb = new StringBuilder();
        sb.append(arrlatitude[0]);
        sb.append("/1,");
        sb.append(arrlatitude[1]);
        sb.append("/1,");
        sb.append(arrlatitude[2]);
        sb.append("/1,");
        return sb.toString();
    }

    public Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",",3);

        String[] stringD = DMS[0].split("/",2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String [] stringM = DMS[1].split("/",2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String [] stringS = DMS[2].split("/",2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD+ (FloatM/60) + (FloatS/3600));
        return result;
    }

    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "chkGpsService");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
//            if(!CameraActivity.this.isFinishing()) {
                // GPS OFF 일때 Dialog 표시
                AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
                gsDialog.setTitle("위치 서비스 설정");
                gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
                gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // GPS설정 화면으로 이동
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create().show();
//            }
            return false;

        } else {
            return true;
        }
    }




}
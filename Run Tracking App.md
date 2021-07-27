# Run Tracking App

## Features

* Track runs
  * Press a button to start/end? Have Google Fit track automatically?
    * https://developers.google.com/fit/android/record
  * Distance
    * From this, calculate pace over some interval (Better to use Google Fit?)
      * Can display pace as a function of time
  * Trace out path
    * Place a marker at the start and end points of the run
    * https://developers.google.com/maps/documentation/android-sdk/polygon-tutorial
    * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial#get-the-location-of-the-android-device-and-position-the-map
    * Automatically adjust zoom to fit entire path in view

* Keep a log of past runs
* Overall statistics
  * Total distance traveled
  * Total time spent running

## What is needed to accomplish this

* Google Maps API
  * Part of Google Play services SDK
  * https://developers.google.com/maps/documentation/android-sdk/start
* Google Fit API
  * Part of Google Play services SDK
  * https://developers.google.com/fit/android/get-started
* UI
  * Navbar
    * https://developer.android.com/reference/androidx/navigation/ui/AppBarConfiguration
    * https://developer.android.com/reference/com/google/android/material/navigation/NavigationView
    * https://developer.android.com/guide/navigation
    * https://developer.android.com/guide/navigation/navigation-principles



### How to get current location of user

background location: https://developer.android.com/training/location/background

We must:

* Check Location permission
* Once granted, call `setMyLocationEnabled()`, build the `FusedLocationProviderClient`, and connect it
  * `mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());`
  * `mGoogleMap.setMyLocationEnabled(true);`

* Once connected, request location updates



To check Location permission:

* Ensure device is running at least Android 6.0 Marshmallow (this version is when dangerous permissions were no longer granted at install time, but must be requested at runtime)
  * Use `ContextCompat.checkSelfPermission(Context, context, String permission)` to check if we have permissions
    * if Location permission already granted: build `FusedLocationProviderClient` and `setMyLocationEnabled()`
  * else: request permission
* If not lower version than 6.0: build `FusedLocationProviderClient` and `setMyLocationEnabled()`



To request permission:

* Use `ActivityCompat.requestPermissions(Activity activity, String[] permissions,int reqCode)`
* Should we show an explanation for why we need permission?
  * <img src="https://i.stack.imgur.com/xLds8.png" alt="enter image description here" style="zoom: 80%;" />

* Unless the user requested we don't ask again, request permission
* From the `ActivityCompat.java` class:
  * If your app does not have the requested permissions the user will be presented with UI for accepting them. After the user has accepted or rejected the requested permissions you will receive a callback reporting whether the permissions were granted or not. Your activity has to implement `ActivityCompat.OnRequestPermissionsResultCallback` and the results of permission requests will be delivered to its `ActivityCompat.OnRequestPermissionsResultCallback.onRequestPermissionsResult(int, String[], int[])` method.

### FusedLocationProviderClient

The main entry point for interacting with the fused location provider.

https://stackoverflow.com/questions/60696903/fusedlocationproviderclient-background-service-for-android-9-and-later

https://droidbyme.medium.com/get-current-location-using-fusedlocationproviderclient-in-android-cb7ebf5ab88e

### How to trace out path of user

#### First, we need the location data

When our `FusedLocationProviderClient` calls `requestLocationUpdates(LocationRequest locationRequest, LocationCallback locationCallback, Looper looper)`:

* This will have `locationRequest` create location requests at some specified interval. When device location information is available, `locationCallback` will call `onLocationResult(LocationResult result)`
  * In this method, we call `locationResult.getLocations()`, which returns locations computed, ordered from oldest to newest.
    * Store the return as `List<Location> locationList`. An example of the contents of this list can be seen below
    * ![](C:\Users\me\Pictures\ListLocation.png)

### Creating polylines to track user's path

### Create a `startRun()` function

Initialize relevant objects/variables:

* `mDistanceTraveled`, `mPolyline`, `mCoords`, `mLastLocationTime`

Through the `FusedLocationProviderClient`, start requesting location updates

* `mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());`


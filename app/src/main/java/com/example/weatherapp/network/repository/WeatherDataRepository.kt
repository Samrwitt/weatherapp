package com.example.weatherapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.google.android.gms.location.Priority
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.example.weatherapp.data.CurrentLocation
import java.io.IOException

class WeatherDataRepository {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation)-> Unit,
        onFailure:()-> Unit
    ){
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location ?: onFailure()
            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }.addOnFailureListener { onFailure() }
    }

    @Suppress("DEPRECIATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = StringBuilder()
                    address.locality?.let { addressText.append(it).append(", ") }
                    address.adminArea?.let { addressText.append(it).append(", ") }
                    address.countryName?.let { addressText.append(it) }
                    return currentLocation.copy(
                        location = addressText.toString()
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return currentLocation
    }
}
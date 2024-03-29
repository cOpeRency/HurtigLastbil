package fr.hurtiglastbil.activites

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.hurtiglastbil.R


class ActivitePrincipale : AppCompatActivity() {
    private val SMS_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        window.decorView.alpha = 0f

        window.insetsController?.hide(WindowInsets.Type.statusBars())

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        }
        finish()
    }
}
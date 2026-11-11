package com.example.dsy1105_002d_vvaldivia_jtapia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation.AppNavigation
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.DSY1105002DVVALDIVIAJTAPIATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DSY1105002DVVALDIVIAJTAPIATheme {
                AppNavigation()
            }
        }
    }
}
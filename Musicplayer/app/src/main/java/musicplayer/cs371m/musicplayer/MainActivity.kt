package musicplayer.cs371m.musicplayer

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import musicplayer.cs371m.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    companion object {
        fun setBackgroundDrawable(button: ImageButton, resourceId: Int) {
            button.setBackgroundResource(resourceId)
            button.tag = resourceId
        }

        fun setBackgroundColor(view: View, color: Int) {
            view.setBackgroundColor(color)
            view.tag = color
        }
    }

    private fun initMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate the menu; this adds items to the action bar if it is present.
                menuInflater.inflate(R.menu.player_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle action bar item clicks here.
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        navController.navigate(R.id.settingsFragment)
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initMenu()

        // Set up our nav graph
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
package ie.wit.pridenjoy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import ie.wit.pridenjoy.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash_screen)

        logoTitle.alpha = 0f
        logoTitle.animate().setDuration(3000).setStartDelay(3000).alpha(1f).withEndAction {
            val i = Intent(this, Login::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
            finish()
        }

        iv_pindrop_rainbow.alpha = 0f
        iv_pindrop_rainbow.animate().setDuration(4000).rotationYBy(3600f).rotationXBy(360f)
            .alpha(1f).withEndAction {
        }
    }
}
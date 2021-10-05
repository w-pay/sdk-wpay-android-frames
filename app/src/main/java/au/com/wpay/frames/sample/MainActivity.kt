package au.com.wpay.frames.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        savedInstanceState ?: run {
            val args = bundleOf(FramesHost.HTML_KEY to "<html><body><div id='cardElement'></div></body></html>")

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FramesHost>(R.id.framesHostContainer, args = args)
            }
        }
    }
}

package me.jiahuan.openrc.device.foreground.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.layout_activity_main.*
import me.jiahuan.openrc.device.R
import me.jiahuan.openrc.device.foreground.base.BaseActivity
import me.jiahuan.openrc.device.foreground.base.BaseViewModel
import me.jiahuan.openrc.device.foreground.fragment.HomeFragment
import me.jiahuan.openrc.device.foreground.fragment.SettingFragment


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var homeFragment: HomeFragment? = null
    private var settingFragment: SettingFragment? = null

    override fun initializeViewModel(): BaseViewModel {
        return BaseViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)

        setSupportActionBar(id_tool_bar)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            id_drawer_layout,
            id_tool_bar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        id_drawer_layout.addDrawerListener(drawerToggle)

        drawerToggle.syncState()

        id_navigation_view.setNavigationItemSelectedListener(this)

        showFragment(R.id.id_menu_home)
    }

    private fun showFragment(id: Int) {
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }
        if (id == R.id.id_menu_home) {
            val fragment = supportFragmentManager.findFragmentByTag(HomeFragment.TAG)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().show(fragment).commit()
            } else {
                if (homeFragment == null) {
                    homeFragment = HomeFragment()
                }
                supportFragmentManager.beginTransaction().add(R.id.id_fragment_container, homeFragment!!, HomeFragment.TAG).commit()
            }
        } else if (id == R.id.id_menu_setting) {
            val fragment = supportFragmentManager.findFragmentByTag(SettingFragment.TAG)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().show(fragment).commit()
            } else {
                if (settingFragment == null) {
                    settingFragment = SettingFragment()
                }
                supportFragmentManager.beginTransaction().add(R.id.id_fragment_container, settingFragment!!, SettingFragment.TAG).commit()
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        showFragment(menuItem.itemId)
        id_drawer_layout.closeDrawers()
        return true
    }
}
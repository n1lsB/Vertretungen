package de.nils_beyer.android.Vertretungen
import kotlinx.android.synthetic.main.activity_login.*


import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.nils_beyer.android.Vertretungen.account.AccountSpinner
import de.nils_beyer.android.Vertretungen.download.UnAuthorizedException


class LoginActivity : AppCompatActivity() {
    var canClose: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_account_spinner.viewOrder = AccountSpinner.ViewOrder.INVERSE
        login_account_spinner.setViewConfig(AccountSpinner.ViewConfig.SHOW_UNREGISTERED)

        // Set canClose:
        // If we have only unregistered Accounts
        // we cannot close the activity
        canClose = !login_account_spinner.hasOnlyUnregistered(applicationContext)


        activity_login_text_version.text = String.format(getString(R.string.info_app_with_version), getAppVersion())

        if (Build.VERSION.SDK_INT >= 23) {
            window.statusBarColor = resources.getColor(R.color.login_color, theme)
        }

        // hide the progressbar
        progressBar.visibility = View.GONE


        // Setup Login Button
        login_btn_login.setOnClickListener({
            onLoginClicked()
        })
    }

    private fun onLoginClicked() {
        login_error.visibility = View.GONE
        hideKeyboard()

        progressBar.visibility = View.VISIBLE
        login_error.isEnabled = false

        // Try to login
        checkPassword({ accepted : Boolean ->
            // on result
            progressBar.visibility = View.GONE
            login_btn_login.isEnabled = true
            if (accepted) {
                canClose = true
                finish()
            } else {
                login_error.visibility = View.VISIBLE
                login_error.text = getString(R.string.login_error)
                val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                // Use new VibrationEffect call on Android Oreo
                // if not available, use the deprecated vibrate method.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibratorService.vibrate(VibrationEffect.createOneShot(
                            100, VibrationEffect.DEFAULT_AMPLITUDE
                    ))
                } else {
                    vibratorService.vibrate(100)
                }
            }
        }, {
            // on error
            progressBar.visibility = View.GONE
            login_btn_login.isEnabled = true

            login_error.visibility = View.VISIBLE
            login_error.text = it.message

            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            // Use new VibrationEffect call on Android Oreo
            // if not available, use the deprecated vibrate method.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibratorService.vibrate(VibrationEffect.createOneShot(
                        100, VibrationEffect.DEFAULT_AMPLITUDE
                ))
            } else {
                vibratorService.vibrate(100)
            }

            Snackbar.make(activity_login, String.format(getString(R.string.activity_login_generic_error_template), it.message), Snackbar.LENGTH_LONG).show()
        })
    }

    override fun finish() {
        // When the User should not return to the MainActivity
        // because he is not logged in,
        // we close the App.
        if (!canClose) {
            finishAffinity()
        } else {
            super.finish()
        }
    }



    /**
     * Makes an Request to the Server to check Username/Password
     * @param checkPasswordCallback Callback for Success/Error/Failure
     */
    private fun checkPassword(callback: (Boolean)->(Unit), error : (Exception) -> Unit) {
        Thread(Runnable {
            try {
                login_account_spinner.getSelectedAccount().tryRegister(this, login_username.text.toString(), login_password.text.toString())
                runOnUiThread { callback(true) }
            } catch (e: UnAuthorizedException) {
                // In case that the authorization
                // credentials are wrong
                // a security Exception is thrown by the
                // DownloadService
                runOnUiThread { callback(false) }
            } catch (e: Exception) {
                // If there is an IOException
                // the onError method will show a
                // Snackbar with further information
                // for the user.
                runOnUiThread { error(e) }
            }
        }).start()
    }

    /**
     * Hides the On-board Keyboard
     */
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Returns the app version name or null if packageManager throws an error
     */
    private fun getAppVersion() : String? {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}

package de.nils_beyer.android.Vertretungen
import kotlinx.android.synthetic.main.activity_login.*


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.nils_beyer.android.Vertretungen.account.AccountSpinner


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
            Snackbar.make(activity_login, getString(R.string.io_error), Snackbar.LENGTH_LONG).show()
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
                login_account_spinner.getSelectedAccount()!!.tryRegister(this, login_username.text.toString(), login_password.text.toString())
                runOnUiThread { callback(true) }
            } catch (e: SecurityException) {
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
}

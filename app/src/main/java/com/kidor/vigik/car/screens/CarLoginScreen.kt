package com.kidor.vigik.car.screens

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.InputCallback
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.car.app.model.signin.InputSignInMethod
import androidx.car.app.model.signin.SignInTemplate
import com.kidor.vigik.R
import timber.log.Timber

private const val PASSWORD_MIN_VALUE = 0
private const val PASSWORD_MAX_VALUE = 9999

/**
 * Screens that displays a sign-in template to find the correct PIN code.
 */
class CarLoginScreen(carContext: CarContext) : Screen(carContext) {

    private val secretPassword: String = IntRange(PASSWORD_MIN_VALUE, PASSWORD_MAX_VALUE).random().toString()
    private var textValue = ""

    override fun onGetTemplate(): Template {
        // Requires Car API 2
        return SignInTemplate.Builder(
            InputSignInMethod.Builder(
                object : InputCallback {
                    override fun onInputTextChanged(text: String) {
                        textValue = text
                    }

                    override fun onInputSubmitted(text: String) {
                        textValue = text
                        checkPassword()
                    }
                }
            )
                .setHint(carContext.getString(R.string.car_login_hint_text))
                .setKeyboardType(InputSignInMethod.KEYBOARD_NUMBER)
                .build()
            )
            .setTitle(carContext.getString(R.string.car_login_label))
            .setHeaderAction(Action.BACK)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(carContext.getString(R.string.car_login_action_help_label))
                            .setOnClickListener {
                                CarToast.makeText(carContext, secretPassword, CarToast.LENGTH_LONG).show()
                                Timber.d("Secret password: $secretPassword")
                            }
                            .build()
                    )
                    .build()
            )
            .setAdditionalText(carContext.getString(R.string.car_login_additional_text))
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.car_login_action_login_label))
                    .setBackgroundColor(CarColor.BLUE)
                    .setOnClickListener(
                        ParkedOnlyOnClickListener.create {
                            checkPassword()
                        }
                    )
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.car_login_action_cancel_label))
                    .setOnClickListener(
                        ParkedOnlyOnClickListener.create { screenManager.pop() }
                    )
                    .build()
            )
            .build()
    }

    private fun checkPassword() {
        CarToast.makeText(
            carContext,
            carContext.getText(
                if (textValue == secretPassword) {
                    R.string.car_login_success_message
                } else {
                    R.string.car_login_error_message
                }
            ),
            CarToast.LENGTH_SHORT
        ).show()
    }
}

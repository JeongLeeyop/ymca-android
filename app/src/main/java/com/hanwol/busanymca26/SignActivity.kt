package com.hanwol.busanymca26

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class SignActivity : AppCompatActivity(), View.OnTouchListener {
    private var rlSignBody: RelativeLayout? = null
    private var llSignAutoLogin: LinearLayout? = null
    private var cbSignAutoLogin: CheckBox? = null
    private var etSignName: EditText? = null
    private var etSignPhone: EditText? = null
    private var btnSignLogin: Button? = null

    private val firebase: FirebaseDatabase = FirebaseDatabase.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        rlSignBody = findViewById(R.id.rl_sign_body)
        llSignAutoLogin = findViewById(R.id.ll_sign_autologin)
        cbSignAutoLogin = findViewById(R.id.cb_sign_autologin)
        etSignName = findViewById(R.id.et_sign_name)
        etSignPhone = findViewById(R.id.et_sign_phone)
        btnSignLogin = findViewById(R.id.btn_sign_login)

        rlSignBody?.setOnTouchListener(this)
        cbSignAutoLogin?.isChecked = false

        onGetUserData("", "")

        llSignAutoLogin?.setOnClickListener {
            cbSignAutoLogin?.isChecked = !cbSignAutoLogin?.isChecked!!
        }

        btnSignLogin?.setOnClickListener {
            UserData.btnState = true
            onGetUserData(etSignName?.text.toString().trim(), etSignPhone?.text.toString().trim())
        }

        if (App.prefs.autoLogin!!) {
            Toast.makeText(this, "자동로그인중...", Toast.LENGTH_SHORT).show()
            onGetUserData(App.prefs.userName!!, App.prefs.userPhone!!)
        }
    }

    private fun onGetUserData(strName: String, strPhone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firebase.getReference(strPhone).addValueEventListener(object : ValueEventListener {
                @SuppressLint("SimpleDateFormat")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (strName != "" && strPhone != "") {
                        if (strName == dataSnapshot.child("kor_name").value.toString()) {
                            if (cbSignAutoLogin!!.isChecked) {
                                App.prefs.autoLogin = true
                                App.prefs.userName = strName
                                App.prefs.userPhone = strPhone
                            }
                            val defaultBirthFormat = SimpleDateFormat("yyyyMMdd")
                            val defaultValidityFormat = SimpleDateFormat("yyyyMMdd")

                            val korDateFormat = SimpleDateFormat("yyyy.MM.dd")
                            val engDateFormat = SimpleDateFormat("dd.MM.yyyy")

                            val korBirthDate: Date = defaultBirthFormat.parse(dataSnapshot.child("birth").value.toString())
                            val engBirthDate: Date = defaultBirthFormat.parse(dataSnapshot.child("birth").value.toString())
                            val korValidityStartDate: Date = defaultValidityFormat.parse(dataSnapshot.child("validity_start").value.toString())
                            val korValidityEndDate: Date = defaultValidityFormat.parse(dataSnapshot.child("validity_end").value.toString())
                            val engValidityStartDate: Date = defaultValidityFormat.parse(dataSnapshot.child("validity_start").value.toString())
                            val engValidityEndDate: Date = defaultValidityFormat.parse(dataSnapshot.child("validity_end").value.toString())

                            UserData.korName = dataSnapshot.child("kor_name").value.toString()
                            UserData.engName = dataSnapshot.child("eng_name").value.toString()
                            UserData.korBirth = korDateFormat.format(korBirthDate)
                            UserData.engBirth = engDateFormat.format(engBirthDate)
                            UserData.korValidity = korDateFormat.format(korValidityStartDate) + " ~ " + korDateFormat.format(korValidityEndDate)
                            UserData.engValidity = "From " + engDateFormat.format(engValidityStartDate) + " to " + engDateFormat.format(engValidityEndDate)
                            UserData.korRound = dataSnapshot.child("round").value.toString() + " 회차"
                            UserData.engRound = "Round " + dataSnapshot.child("round").value.toString()

                            startActivity(Intent(this@SignActivity, MainActivity::class.java))
                            finish()
                            UserData.btnState = false
                            if (!UserData.signState) {
                                Toast.makeText(this@SignActivity, "로그인 성공", Toast.LENGTH_LONG).show()
                                UserData.signState = true
                                UserData.upDateState = true
                            }
                        } else {
                            if (App.prefs.autoLogin!!) {
                                runBlocking {
                                    App.prefs.autoLogin = false
                                    App.prefs.userName = ""
                                    App.prefs.userPhone = ""
                                    UserData.korName = ""
                                    UserData.engName = ""
                                    UserData.korBirth = ""
                                    UserData.engBirth = ""
                                    UserData.korValidity = ""
                                    UserData.engValidity = ""
                                    UserData.korRound = ""
                                    UserData.engRound = ""
                                    UserData.signState = false

                                    Toast.makeText(this@SignActivity, "자동로그인 실패\n자동로그인이 해제됩니다.", Toast.LENGTH_LONG).show()
                                    Toast.makeText(this@SignActivity, "네트워크 확인, 다른 계정으로 로그인 해 주세요.", Toast.LENGTH_LONG).show()
                                    delay(1000)
                                }

                                moveTaskToBack(true)
                                finishAndRemoveTask()
                                android.os.Process.killProcess(android.os.Process.myPid())
                            } else
                                if (UserData.signState) {
                                    runBlocking {
                                        App.prefs.autoLogin = false
                                        App.prefs.userName = ""
                                        App.prefs.userPhone = ""
                                        UserData.korName = ""
                                        UserData.engName = ""
                                        UserData.korBirth = ""
                                        UserData.engBirth = ""
                                        UserData.korValidity = ""
                                        UserData.engValidity = ""
                                        UserData.korRound = ""
                                        UserData.engRound = ""
                                        UserData.signState = false

                                        Toast.makeText(this@SignActivity, "계정이 변경되었습니다.\n로그인이 해제됩니다.", Toast.LENGTH_LONG).show()
                                        delay(1000)
                                    }

                                    moveTaskToBack(true)
                                    finishAndRemoveTask()
                                    android.os.Process.killProcess(android.os.Process.myPid())
                                } else
                                    Toast.makeText(this@SignActivity, "이름과 전화번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } else
                        if (UserData.btnState) {
                            Toast.makeText(this@SignActivity, "입력창을 채워주세요.", Toast.LENGTH_LONG).show()
                            UserData.btnState = false
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        when (p0?.id) {
            R.id.rl_sign_body -> {
                etSignName?.clearFocus()
                etSignPhone?.clearFocus()
                hideKeyBoard()
            }
        }
        return true
    }

    fun hideKeyBoard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(etSignName?.windowToken, 0)
        inputMethodManager.hideSoftInputFromWindow(etSignPhone?.windowToken, 0)
    }
}
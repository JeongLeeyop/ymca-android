package com.hanwol.busanymca26

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var tvMainKorName: TextView? = null
    private var tvMainEngName: TextView? = null
    private var tvMainKorBirth: TextView? = null
    private var tvMainEngBirth: TextView? = null
    private var tvMainKorValidity: TextView? = null
    private var tvMainEngValidity: TextView? = null
    private var tvMainKorCount: TextView? = null
    private var tvMainEngCount: TextView? = null
    private var ibMainLogOut: ImageButton? = null

    private var ibMainBlog: ImageButton? = null
    private var ibMainFacebook: ImageButton? = null
    private var ibMainInsta: ImageButton? = null
    private var ibMainYoutube: ImageButton? = null
    private var ibMainWeb: ImageButton? = null

    private val firebase: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMainKorName = findViewById(R.id.tv_main_korname)
        tvMainEngName = findViewById(R.id.tv_main_engname)
        tvMainKorBirth = findViewById(R.id.tv_main_korbirth)
        tvMainEngBirth = findViewById(R.id.tv_main_engbirth)
        tvMainKorValidity = findViewById(R.id.tv_main_korvalidity)
        tvMainEngValidity = findViewById(R.id.tv_main_engvalidity)
        tvMainKorCount = findViewById(R.id.tv_main_korcount)
        tvMainEngCount = findViewById(R.id.tv_main_engcount)
        ibMainLogOut = findViewById(R.id.ib_main_logout)
        ibMainBlog = findViewById(R.id.ib_main_blog)
        ibMainFacebook = findViewById(R.id.ib_main_facebook)
        ibMainInsta = findViewById(R.id.ib_main_insta)
        ibMainYoutube = findViewById(R.id.ib_main_youtube)
        ibMainWeb = findViewById(R.id.ib_main_web)

        tvMainKorName?.text = UserData.korName
        tvMainEngName?.text = UserData.engName
        tvMainKorBirth?.text = UserData.korBirth
        tvMainEngBirth?.text = UserData.engBirth
        tvMainKorValidity?.text = UserData.korValidity
        tvMainEngValidity?.text = UserData.engValidity
        tvMainKorCount?.text = UserData.korRound
        tvMainEngCount?.text = UserData.engRound

        onUpdateUserData()

        ibMainLogOut?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃하고 자동로그인 기능을 해제하시겠습니까?")
                .setPositiveButton("OK") { dialog, _ ->
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

                    Toast.makeText(this, "로그아웃되었습니다.\n다시 로그인 해 주세요.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignActivity::class.java))
                    finish()

                    dialog.dismiss()
                }
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        ibMainBlog?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/ymcabs/")))
        }
        ibMainFacebook?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ymcabs/")))
        }
        ibMainInsta?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/busanymca/")))
        }
        ibMainYoutube?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC5vrTqvU179T2zqukDyAhTA/")))
        }
        ibMainWeb?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://psymca.or.kr/")))
        }
    }

    private fun onUpdateUserData() {
        CoroutineScope(Dispatchers.Default).launch {
            firebase.getReference(App.prefs.userPhone.toString()).addValueEventListener(object : ValueEventListener {
                @SuppressLint("SimpleDateFormat")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Toast.makeText(this@MainActivity, "서버에서 정보가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("종료")
            .setMessage("종료하시겠습니까?")
            .setPositiveButton("예") { dialog, _ ->
                dialog.dismiss()
                moveTaskToBack(true)
                finishAndRemoveTask()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
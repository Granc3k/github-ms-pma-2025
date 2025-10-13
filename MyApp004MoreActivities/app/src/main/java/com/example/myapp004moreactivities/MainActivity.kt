package com.example.myapp004moreactivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSecondAct = findViewById<Button>(R.id.btnSecondAct)
        val etNickname = findViewById<EditText>(R.id.etNickname)  // tady má být EditText, ne Button!

        btnSecondAct.setOnClickListener {
            val nickname = etNickname.text.toString()  // tady vezmeme text
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("NICK_NAME", nickname)  // sem už jde String, takže OK
            startActivity(intent)
        }
    }
}

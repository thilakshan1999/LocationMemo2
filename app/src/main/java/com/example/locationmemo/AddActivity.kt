package com.example.locationmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnView = findViewById<Button>(R.id.btnView)

        // Click even of the add button.
        btnAdd.setOnClickListener { view ->
            addRecord(view)
        }
        btnView.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
    //Method for saving the employee records in database
    private fun addRecord(view: View) {
        val etName = findViewById<EditText>(R.id.etName)
        val etLatitude = findViewById<EditText>(R.id.etLatitude)
        val etLongitude = findViewById<EditText>(R.id.etLongitude)

        val name = etName.text.toString()
        val latitude = etLatitude.text.toString().toFloat()
        val longitude = etLongitude.text.toString().toFloat()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (name.isNotEmpty() && !latitude.isNaN()&& !longitude.isNaN()) {
            val status =
                databaseHandler.addLocation(LocationModel(0, name, latitude,longitude))
            if (status > -1) {
                Toast.makeText(this.application, "Record saved", Toast.LENGTH_LONG).show()
                etName.text.clear()
                etLatitude.text.clear()
                etLongitude.text.clear()
            }
        } else {
            Toast.makeText(
                this.application,
                "Fields cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
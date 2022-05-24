package com.example.locationmemo

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val i = Intent(this, AddActivity::class.java)
            startActivity(i)
        }
        setupListofDataIntoRecyclerView()
    }
    private fun getItemsList(): ArrayList<LocationModel> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val locList: ArrayList<LocationModel> = databaseHandler.viewLocation()

        return locList
    }

    fun setupListofDataIntoRecyclerView() {
        val rvItemsList = findViewById<RecyclerView>(R.id.rvItemsList)
        val tvNoRecordsAvailable = findViewById<TextView>(R.id.tvNoRecordsAvailable)

        if (getItemsList().size > 0) {
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(this, getItemsList(),this)
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsList.adapter = itemAdapter
        } else {

            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    fun updateRecordDialog(loc: LocationModel) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.findViewById<EditText>(R.id.etUpdateName).setText(loc.name)
        updateDialog.findViewById<EditText>(R.id.etUpdateLatitude).setText(loc.latitude.toString())
        updateDialog.findViewById<EditText>(R.id.etUpdateLongitude).setText(loc.longitude.toString())

        updateDialog.findViewById<TextView>(R.id.tvUpdate).setOnClickListener(View.OnClickListener {

            val name = updateDialog.findViewById<EditText>(R.id.etUpdateName).text.toString()
            val latitude = updateDialog.findViewById<EditText>(R.id.etUpdateLatitude).text.toString().toFloat()
            val longitude= updateDialog.findViewById<EditText>(R.id.etUpdateLongitude).text.toString().toFloat()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !latitude.isNaN() && !longitude.isNaN()) {
                val status =
                    databaseHandler.updateLocation(LocationModel(loc.id, name, latitude,longitude))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()

                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        updateDialog.findViewById<TextView>(R.id.tvCancel).setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        //Start the dialog and display it on screen.
        updateDialog.show()
    }

    fun deleteRecordAlertDialog(loc: LocationModel) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${loc.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteLocation(LocationModel(loc.id, "", 0f,0f))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    override fun onItemClick(position: Int,loc: LocationModel) {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.visibility = View.GONE

        Log.i("position", "$position")
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val mapsFragment = MapsFragment()

        val bundle = Bundle()
        bundle.putString("name", loc.name)
        bundle.putFloat("latitude", loc.latitude)
        bundle.putFloat("longitude", loc.longitude)
        Log.i("name", loc.latitude.toString())
        mapsFragment.arguments = bundle
        fragmentTransaction.add(R.id.aMain, mapsFragment).commit()
    }
}
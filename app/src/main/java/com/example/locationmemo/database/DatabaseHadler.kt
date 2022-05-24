package com.example.locationmemo.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "LocationDB"

        private val TABLE_CONTACTS = "LocationTable"

        private val KEY_ID = "_id"
        private val KEY_NAME = "name"
        private val KEY_LATITUDE = "latitude"
        private val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LATITUDE + " REAL,"+  KEY_LONGITUDE + " REAL" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    /**
     * Function to insert data
     */
    fun addLocation(loc: LocationModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, loc.name)
        contentValues.put(KEY_LATITUDE, loc.latitude)
        contentValues.put(KEY_LONGITUDE, loc.longitude)

        // Inserting employee details using insert query.
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }

    /**
     * Function to view records
     */
    @SuppressLint("Range")
    fun viewLocation(): ArrayList<LocationModel> {

        val locList: ArrayList<LocationModel> = ArrayList<LocationModel>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var latitude: Float
        var longitude: Float

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                latitude = cursor.getFloat(cursor.getColumnIndex(KEY_LATITUDE))
                longitude = cursor.getFloat(cursor.getColumnIndex(KEY_LONGITUDE))

                val loc = LocationModel(id = id, name = name, latitude = latitude, longitude = longitude)
                locList.add(loc)

            } while (cursor.moveToNext())
        }
        return locList
    }

    /**
     * Function to Update record
     */
    fun updateLocation(loc: LocationModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, loc.name)
        contentValues.put(KEY_LATITUDE, loc.latitude)
        contentValues.put(KEY_LONGITUDE,loc.longitude)

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + loc.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }

    /**
     * Function to delete record
     */
    fun deleteLocation(loc: LocationModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, loc.id) // EmpModelClass id
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + loc.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
}
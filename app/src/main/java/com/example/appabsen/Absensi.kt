package com.example.appabsen

import android.os.Parcel
import android.os.Parcelable

data class Absensi(
    val status: String,
    val nama: String,
    val nim: String,
    val mataKuliah: String,
    val keterangan: String,
    val tanggal: String,
    val imageUrl: String,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?:"",
        parcel.readString() ?:"",
        parcel.readString() ?:"",
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(status)
        dest.writeString(nama)
        dest.writeString(nim)
        dest.writeString(mataKuliah)
        dest.writeString(keterangan)
        dest.writeString(tanggal)
        dest.writeString(imageUrl)

    }

    companion object CREATOR : Parcelable.Creator<Absensi> {
        override fun createFromParcel(parcel: Parcel): Absensi {
            return Absensi(parcel)
        }

        override fun newArray(size: Int): Array<Absensi?> {
            return arrayOfNulls(size)
        }
    }
}
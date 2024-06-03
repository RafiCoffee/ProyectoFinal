package com.example.proyectofinal.data.models

import android.os.Parcel
import android.os.Parcelable

data class Usuario(var id: String, var nombre: String, var email: String, var clave: String, var foto: String?, var idAmigo: String, var Amigos: List<Amigo>): Parcelable {
    constructor() : this("", "", "", "", "", "", emptyList())

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Amigo.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeString(email)
        parcel.writeString(clave)
        parcel.writeString(foto)
        parcel.writeString(idAmigo)
        parcel.writeList(Amigos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usuario> {
        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(parcel)
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
    }
}
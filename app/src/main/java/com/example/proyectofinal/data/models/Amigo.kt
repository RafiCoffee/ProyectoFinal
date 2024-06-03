package com.example.proyectofinal.data.models

import android.os.Parcel
import android.os.Parcelable

data class Amigo(var id: String, var nombre: String, var foto: String?, var idAmigo: String, var estadoSolicitud: Int): Parcelable {
    constructor() : this("", "", "", "", 0)

    /**
     * Estado solicitud
     * Valor 0 -> No se ha solicitado por ninguna de las dos partes (Valor Por Defecto)
     * Valor 1 -> Yo como usuario lo he solicitado pero no ha sido aceptado
     * Valor 2 -> Otro usuario me lo ha solicitado pero no lo he aceptado
     * Valor 3 -> La solicitud la han aceptado ambos usuarios
     */

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(foto)
        parcel.writeString(idAmigo)
        parcel.writeInt(estadoSolicitud)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Amigo> {
        override fun createFromParcel(parcel: Parcel): Amigo {
            return Amigo(parcel)
        }

        override fun newArray(size: Int): Array<Amigo?> {
            return arrayOfNulls(size)
        }
    }
}
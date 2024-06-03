package com.example.proyectofinal.data.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
class Notificacion(var id: String, var tipoNotificacion: Int, var idUsuario: String, var idInfo: String, var seMuestra: Boolean): Parcelable {
    constructor() : this("", 0, "", "", false)

    /**
     * Tipo Notificación
     * Valor 1 -> Notificación De Tarea
     * Valor 2 -> Notificación De Amigo
     */

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readBoolean()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(tipoNotificacion)
        parcel.writeString(idUsuario)
        parcel.writeString(idInfo)
        parcel.writeBoolean(seMuestra)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notificacion> {
        override fun createFromParcel(parcel: Parcel): Notificacion {
            return Notificacion(parcel)
        }

        override fun newArray(size: Int): Array<Notificacion?> {
            return arrayOfNulls(size)
        }
    }
}
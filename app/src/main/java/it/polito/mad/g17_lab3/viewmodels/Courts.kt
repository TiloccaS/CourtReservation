package it.polito.mad.g17_lab3.viewmodels

import com.google.firebase.firestore.DocumentReference

class Courts {
    private var id:DocumentReference?=null
    private var name=""
    private var address=""
    private var opening=""
    private var closing=""
    fun getName():String{
        return name
    }
    fun setName(newName:String){
        name=newName
    }
    fun setId(newId:DocumentReference?){
        id=newId
    }
    fun getId():DocumentReference?{
        return id
    }
    fun getAddress():String{
        return address
    }
    fun setAddress(newAddress:String){
        address=newAddress
    }
    fun getOpening():String{
        return opening
    }
    fun setOpening(newOpening:String){
        opening=newOpening
    }
    fun getClosing():String{
        return closing
    }
    fun setClosing(newClosing:String){
        closing=newClosing
    }
}
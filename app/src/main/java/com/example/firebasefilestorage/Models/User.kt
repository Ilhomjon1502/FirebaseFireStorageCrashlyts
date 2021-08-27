package com.example.firebasefilestorage.Models

class User {
    var name:String? = null
    var age:Int? = null
    var imageUri:String? = null


    constructor()
    constructor(name: String?, age: Int?, imageUri: String?) {
        this.name = name
        this.age = age
        this.imageUri = imageUri
    }
}
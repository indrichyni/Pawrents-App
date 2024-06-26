package com.android.pawrents.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.android.pawrents.data.model.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PetsViewModel : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("pets")

    private val _petsSortedByTimestamp = MutableLiveData<List<Pet>>()
    val petsSortedByTimestamp: LiveData<List<Pet>> get() = _petsSortedByTimestamp

    private val _petsRandomOrder = MutableLiveData<List<Pet>>()
    val petsRandomOrder: LiveData<List<Pet>> get() = _petsRandomOrder

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    private val _petsFiltered = MutableLiveData<List<Pet>>()
    val petsFiltered: LiveData<List<Pet>> get() = _petsFiltered

    private var currentCategory: String = "all"
    private var searchText: String = ""

    init {
        fetchPetsSortedByTimestamp()
        fetchPetsRandomOrder()
    }

    private fun fetchPetsSortedByTimestamp() {
        _isLoading.value = true
        database.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val petsList = mutableListOf<Pet>()
                for (petSnapshot in dataSnapshot.children) {
                    val pet = petSnapshot.getValue(Pet::class.java)
                    if (pet != null) {
                        petsList.add(pet)
                    }
                }
                petsList.sortByDescending { it.timestamp }
                _isLoading.value = false
                _petsSortedByTimestamp.value = petsList
                applyFilters()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _isLoading.value = false
                _errorMsg.value  =databaseError.message
            }
        })
    }

    fun filterPetsByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    fun searchPets(query: String) {
        searchText = query
        applyFilters()
    }

    private fun applyFilters() {
        val filteredList = _petsSortedByTimestamp.value?.filter {pet ->
            val matchesCategory = if(currentCategory == "all") true else pet.category == currentCategory
            val matchesSearch = pet.name.contains(searchText, ignoreCase = true)
            matchesCategory && matchesSearch }
        _petsFiltered.value = filteredList ?: listOf()

    }

    private fun fetchPetsRandomOrder() {
        _isLoading.value = true
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val petsList = mutableListOf<Pet>()
                for (petSnapshot in dataSnapshot.children) {
                    val pet = petSnapshot.getValue(Pet::class.java)
                    if (pet != null) {
                        petsList.add(pet)
                    }
                }
                petsList.shuffle()
                _isLoading.value = false
                _petsRandomOrder.value = petsList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _isLoading.value = false
                _errorMsg.value  =databaseError.message
            }
        })
    }
}
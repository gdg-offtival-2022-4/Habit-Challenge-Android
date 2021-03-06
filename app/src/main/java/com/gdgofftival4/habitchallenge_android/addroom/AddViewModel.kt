package com.gdgofftival4.habitchallenge_android.addroom

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdgofftival4.habitchallenge_android.addroom.model.*
import com.gdgofftival4.habitchallenge_android.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class AddViewModel(
    private val addRoomService: PostAddRoomService = RetrofitClient.instance.create(PostAddRoomService::class.java)
) : ViewModel() {

    private val _categoryUiModel = MutableLiveData<List<CategoryUiModel>>()
    val categoryUiModel: LiveData<List<CategoryUiModel>>
        get() = _categoryUiModel

    fun clickCategory(idx: Int) {
        val before = _categoryUiModel.value?:return
        val after = before.mapIndexed { index, categoryUiModel ->
            if(index == idx){
                _addroomUiModel.update {
                    it.copy(category = categoryUiModel.category)
                }
                categoryUiModel.copy(isSelected = true)
            }
            else{
                categoryUiModel.copy(isSelected = false)
            }
        }
        _categoryUiModel.value = after
    }

    private val _addroomUiModel = MutableStateFlow(AddRoomRequest())
    val addroomUiModel: StateFlow<AddRoomRequest>
        get() = _addroomUiModel

    private val _addRoomResponse = MutableLiveData<AddRoomResponse>()
    val addRoomResponse: LiveData<AddRoomResponse>
        get() = _addRoomResponse

    fun onDoneAddRoom() {
        Log.d(javaClass.simpleName, addroomUiModel.value.toString())
        val request = addroomUiModel.value
        with(request) {
            if(this.title != null && this.description != null && this.category != null){
                // Todo: Add Room 호출
                val request = PostAddRoomRequest(this.title, this.category.idx, this.description)
                viewModelScope.launch {
                    with(addRoomService.getHommeList(request)) {
                        if(this.isSuccessful){
                            _addRoomResponse.postValue(this.body())
                        }
                    }
                }
            }
        }
    }

    fun onUpdatetitle(title: String) {
        _addroomUiModel.update {
            it.copy(title = title)
        }
    }

    fun onUpdateDescription(description: String) {
        _addroomUiModel.update {
            it.copy(description = description)
        }
    }

    init {
        val categoryList = Category.values().map {
            CategoryUiModel(category = it, isSelected = false)
        }
        _categoryUiModel.value = categoryList
    }
}
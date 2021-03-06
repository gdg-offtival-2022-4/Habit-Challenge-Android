package com.gdgofftival4.habitchallenge_android.addroom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdgofftival4.habitchallenge_android.addroom.adapter.CategoryAdapter
import com.gdgofftival4.habitchallenge_android.addroom.model.CategoryUiModel
import com.gdgofftival4.habitchallenge_android.addroom.model.Category
import com.gdgofftival4.habitchallenge_android.base.BaseBindingActivity
import com.gdgofftival4.habitchallenge_android.databinding.ActivityAddroomBinding
import com.gdgofftival4.habitchallenge_android.room.RoomActivity

class AddRoomActivity : BaseBindingActivity<ActivityAddroomBinding>(ActivityAddroomBinding::inflate) {

    private val viewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryAdapter = CategoryAdapter(
            onItemClick = {
                // Todo
                viewModel.clickCategory(it)
            }
        )

        binding.categoryRecycler.run {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
        }

        viewModel.categoryUiModel.observe(this) {
            categoryAdapter.addAll(it)
        }

        viewModel.addRoomResponse.observe(this) {
            if(it.room_id.isNotEmpty()) {
                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("roomId", it.room_id)
                intent.putExtra("roomTitle", binding.titleEdit.text.toString())
                intent.putExtra("roomContents", binding.descriptEdit.text.toString())
                startActivity(intent)
                finish()
            }
        }

        binding.titleEdit.doAfterTextChanged {
            viewModel.onUpdatetitle(it?.toString().orEmpty())
        }

        binding.descriptEdit.doAfterTextChanged {
            viewModel.onUpdateDescription(it?.toString().orEmpty())
        }

        binding.okBtn.setOnClickListener {
            viewModel.onDoneAddRoom()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
package com.example.carscanner

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var phoneListContainer: LinearLayout
    private lateinit var addPhoneNumber: TextView
    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputCar: EditText
    private lateinit var editProfile: TextView
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        inputName = view.findViewById(R.id.userNameEdit)
        inputEmail = view.findViewById(R.id.userEmailEdit)
        inputCar = view.findViewById(R.id.userCarEdit)
        editProfile = view.findViewById(R.id.editProfile)

        val suddenWarningSwitch = view.findViewById<Switch>(R.id.suddenWarningSwitch)

        var suddenWarningActive = LocalStore.getBoolean(requireContext(), "suddenWarningActive", true)
        var warningCount = LocalStore.getInt(requireContext(), "warningCount", 3)
        var suddenCallingNums = LocalStore.getStringArray(requireContext(), "suddenCallingNum")
        var suddenCallingNum = ""
        if (suddenCallingNums.isEmpty()){
            suddenCallingNums = listOf("112")
            LocalStore.putStringArray(requireContext(), "suddenCallingNum", suddenCallingNums)
            suddenCallingNum = "112"
        }
        else
            suddenCallingNum = suddenCallingNums.first()

        suddenWarningSwitch.isChecked = suddenWarningActive

        val items = listOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        val dropdown = view.findViewById<AutoCompleteTextView>(R.id.suddenWarningCount)
        dropdown.setAdapter(adapter)
        dropdown.setText(warningCount.toString(), false)

        val callPhoneEdit = view.findViewById<EditText>(R.id.callPhoneEdit)
        val editCallPhone = view.findViewById<TextView>(R.id.editCallPhone)

        callPhoneEdit.setHint(suddenCallingNum)
        dropdown.setOnItemClickListener { _, _, position, _ ->
            LocalStore.putInt(requireContext(), "warningCount", position+1)
        }

         editProfile.setOnClickListener {
            isEditing = !isEditing
            setEditMode(inputName, isEditing)
            setEditMode(inputEmail, isEditing)
            setEditMode(inputCar, isEditing)

            if (isEditing) {
                editProfile.text = "저장하기"
                inputName.requestFocus()
            } else {
                editProfile.text = "수정하기"
                Toast.makeText(requireContext(), "정보가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        suddenWarningSwitch.setOnCheckedChangeListener {_, isChecked ->
            LocalStore.putBoolean(requireContext(), "suddenWarningActive", isChecked)
        }

        editCallPhone.setOnClickListener {
            var editingMode = !callPhoneEdit.isEnabled
            setEditMode(callPhoneEdit, editingMode)
            if (editingMode){
                LocalStore.removeStringFromArray(requireContext(), "suddenCallingNum", suddenCallingNum)
                suddenCallingNum = callPhoneEdit.text.toString()
                if (suddenCallingNum.toIntOrNull() != null){
                    LocalStore.putStringArray(requireContext(), "suddenCallingNum", listOf(suddenCallingNum))
                }
                else {
                    LocalStore.putStringArray(requireContext(), "suddenCallingNum", listOf("112"))
                }
            }
        }

        return view
    }

    private fun setEditMode(editText: EditText, editingMode:Boolean) {
        editText.isEnabled = editingMode
        if (editingMode){
            //editText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
        else {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
/*
    private fun addNewPhoneNumber(number: String) {
        val context = requireContext()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 12, 12, 12)
            setBackgroundResource(R.drawable.phone_item_background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val numberInput = EditText(context).apply {
            setText(number)
            textSize = 16f
            isEnabled = false
            isFocusable = false
            isFocusableInTouchMode = false
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val editIcon = ImageView(context)
        var isEditingNumber = false

        editIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null))
        editIcon.setPadding(16, 0, 16, 0)

        editIcon.setOnClickListener {
            isEditingNumber = !isEditingNumber
            if (isEditingNumber) {
                setEditMode(numberInput, true)
                numberInput.requestFocus()
                editIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_save, null))
            } else {
                setEditMode(numberInput, false)
                editIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null))
                Toast.makeText(context, "번호가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        val deleteIcon = ImageView(context).apply {
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, null))
            setPadding(16, 0, 0, 0)
            setOnClickListener {
                phoneListContainer.removeView(container)
            }
        }

        container.addView(numberInput)
        container.addView(editIcon)
        container.addView(deleteIcon)
        phoneListContainer.addView(container)
    }*/
}

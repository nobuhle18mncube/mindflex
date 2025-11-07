package com.example.mindflex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TaskManager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.task)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.task)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Reference UI elements ---
        val btnBack = findViewById<ImageView>(R.id.btnback)
        val btnAddTask = findViewById<ImageView>(R.id.btnAddTask)
        val taskList = findViewById<LinearLayout>(R.id.task_list)

        // --- Back button behavior ---
        btnBack.setOnClickListener {
            finish() // close this screen and go back
        }

        // --- Add Task button behavior ---
        btnAddTask.setOnClickListener {
            showAddTaskDialog(taskList)
        }
    }

    /**
     * Shows a dialog where the user can type a task name and adds it to the list
     */
    private fun showAddTaskDialog(taskList: LinearLayout) {
        // Create an input field
        val input = EditText(this)
        input.hint = "Enter task name"

        // Build an AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(input)
            .setPositiveButton("Add") { dialog, _ ->
                val taskName = input.text.toString().trim()
                if (taskName.isNotEmpty()) {
                    addTaskToList(taskList, taskName)
                    Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
                    Log.d("TaskManager", "Added task: $taskName")
                } else {
                    Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * Adds a new CheckBox to the LinearLayout with the given text
     */
    private fun addTaskToList(taskList: LinearLayout, taskName: String) {
        val newTask = CheckBox(this)
        newTask.text = taskName
        newTask.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Completed: $taskName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unchecked: $taskName", Toast.LENGTH_SHORT).show()
            }
        }

        // Add the checkbox to the layout
        taskList.addView(newTask)
    }
}

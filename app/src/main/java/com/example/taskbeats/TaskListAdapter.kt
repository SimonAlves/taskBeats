package com.example.taskbeats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

 class TaskListAdapter(
     private val openTaskDetailView:(task:Task) -> Unit
     ): androidx.recyclerview.widget.ListAdapter <Task, TaskListViewHolder>(TaskListAdapter) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
         val view: View =
             LayoutInflater
                 .from(parent.context)
                 .inflate(R.layout.item_task, parent, false)
         return TaskListViewHolder(view)
     }
     companion object : DiffUtil.ItemCallback <Task>(){
         override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
             return oldItem == newItem
         }

         override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
             return oldItem.title == newItem.title &&
                     oldItem.descriptor == newItem.descriptor
         }
     }

     override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
         val task = getItem(position)
         holder.bind(task,openTaskDetailView)
     }
 }
class TaskListViewHolder(
    private val view:View): RecyclerView.ViewHolder(view){

    private val tvTitle = view.findViewById<TextView>(R.id.tv_task_title)
    private val tvDescription = view.findViewById<TextView>(R.id.tv_task_description)

 fun bind (task: Task,
           openTaskDetailView:(task:Task) -> Unit) {
           tvTitle.text=task.title
           tvDescription.text=task.descriptor

           view.setOnClickListener {
               openTaskDetailView.invoke(task)

           }
 }
}

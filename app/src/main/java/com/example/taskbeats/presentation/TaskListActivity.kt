package com.example.taskbeats.presentation

import android.app.Activity
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskbeats.R
import com.example.taskbeats.data.AppDataBase
import com.example.taskbeats.data.Task
import com.example.taskbeats.taskbeatsApplication
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    // Lista de tarefas
    private lateinit var ctnContent: LinearLayout

    // Adaptador para exibir a lista de tarefas
    private val adapter: TaskListAdapter by lazy {
        TaskListAdapter(::onListItemClicked)
    }

    private val viewModel:TaskListViewModel by lazy {
        TaskListViewModel.create(application)
    }
    lateinit var database : AppDataBase
    private val dao by lazy {
        database.taskDao()
    }


    // Registro da atividade para obter resultados
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        // Verifica se o resultado é OK (resultado esperado)
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            val task: Task = taskAction.task
            // Verifica o tipo de ação realizada (DELETE, CREATE ou UPDATE)
            when (taskAction.actionType) {
                ActionType.DELETE.name -> deleteById(task.id)
                ActionType.CREATE.name -> insertIntoDataBase(task)
                ActionType.UPDATE.name -> updateIntoDataBase(task)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taks_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        ctnContent = findViewById(R.id.ctn_content)

        //Recycler view
        val rvtasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvtasks.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)

        }
    }

    override fun onStart() {
        super.onStart()
        database = (application as taskbeatsApplication).getAppDataBase()
        Log.d("SimonTeste",database.toString())

        ListFromDataBase()
    }

    private fun insertIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.insert(task)
            ListFromDataBase()
        }

    }

    private fun updateIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.update(task)
            ListFromDataBase()
        }

    }
    private fun deleteAll(){
        CoroutineScope(IO).launch {
            dao.deleteAll()
            ListFromDataBase()
        }
}
    private fun deleteById(id:Int){
        CoroutineScope(IO).launch {
            dao.deleteById(id)
            ListFromDataBase()
        }
    }
        private fun ListFromDataBase() {
            CoroutineScope(IO).launch {
                val myDataBaseList: List<Task> = dao.getAll()
                adapter.submitList(myDataBaseList)
            }
        }



    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task?) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_list,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.delete_all_Task ->{
             //deletar todas as tarefas
                deleteAll()
                true
            }
            else  -> super.onContextItemSelected(item)
        }

    }

}

enum class ActionType {
    DELETE,
    UPDATE,
    CREATE,
}

data class TaskAction(
    val task: Task,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT "

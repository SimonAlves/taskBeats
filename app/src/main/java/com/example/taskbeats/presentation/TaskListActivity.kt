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
import androidx.lifecycle.Observer
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
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        // Verifica se o resultado é OK (resultado esperado)
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            // Executa a ação recebida pelo resultado
            viewModel.execute(taskAction)

        }
    }
    // Executa a ação recebida pelo resultado
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o layout da Activity a partir do arquivo XML activity_taks_list
        setContentView(R.layout.activity_taks_list)
        // Configura a ActionBar (barra superior) para usar a toolbar definida no layout
        setSupportActionBar(findViewById(R.id.toolbar))
        // Encontra o LinearLayout que contém o conteúdo da lista de tarefas
        ctnContent = findViewById(R.id.ctn_content)

        // Configura o RecyclerView para exibir a lista de tarefas
        val rvtasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvtasks.adapter = adapter
        // Encontra o FloatingActionButton (botão de adicionar) e define o OnClickListener
        // para abrir a tela de detalhes da tarefa com a tarefa sendo nula (não existe tarefa a ser editada)
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)

        }
    }
    // O método onStart é chamado quando a Activity está visível para o usuário
    override fun onStart() {
        super.onStart()
        // Carrega a lista de tarefas a partir do banco de dados
        ListFromDataBase()
    }
    // Função para deletar todas as tarefas
    private fun deleteAll() {
        // Cria uma ação para deletar todas as tarefas e a envia para o ViewModel executar
        val taskAction = TaskAction(null, ActionType.DELETE_ALL.name)
        viewModel.execute(taskAction)
    }
    // Função para buscar a lista de tarefas a partir do banco de dados e exibi-la no RecyclerView
        private fun ListFromDataBase() {
                // Observer para observar mudanças na lista de tarefas
                val listObserver = Observer<List<Task>>{ listTask ->
                    // Verifica se a lista de tarefas está vazia para mostrar ou esconder o conteúdo
                    if(listTask.isEmpty()) {
                        ctnContent.visibility = View.VISIBLE
                    }else{
                        ctnContent.visibility = View.GONE
                    }
                    // Envia a lista de tarefas atualizada para o adaptador para exibição no RecyclerView
                    adapter.submitList(listTask)
                }
        // LiveData que contém a lista de tarefas, o Observer será notificado quando a lista for atualizada
               viewModel.taskListLiveData.observe(this@MainActivity,listObserver)
            }
    // Função para mostrar uma mensagem usando o Snackbar
    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }
    // Função chamada quando um item da lista é clicado, abre a tela de detalhes da tarefa
    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }
    // Função para abrir a tela de detalhes da tarefa, recebe a tarefa como parâmetro
    private fun openTaskListDetail(task: Task?) {
        // Cria o Intent para abrir a tela de detalhes da tarefa e inicia a ActivityForResult
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }
    // O método onCreateOptionsMenu é chamado para criar o menu de opções na ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_list,menu)
        return true
    }
    // O método onOptionsItemSelected é chamado quando uma opção do menu é selecionada
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.delete_all_Task ->{
                // Chamada para deletar todas as tarefas
                deleteAll()
                true
            }
            else  -> super.onContextItemSelected(item)
        }

    }

}
// Enumeração para representar os tipos de ação que podem ser executados em uma tarefa

enum class ActionType {
    DELETE,
    DELETE_ALL,
    UPDATE,
    CREATE,
}
// Classe que representa uma ação a ser executada em uma tarefa
data class TaskAction(
    val task: Task?,
    val actionType: String
) : Serializable
// Constante para passar o resultado de uma ação entre Activities
const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT "

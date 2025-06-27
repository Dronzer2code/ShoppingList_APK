package com.example.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import java.util.jar.Attributes.Name

data class ShoppingItem (
    val id : Int,
    val name : String,
    val quantity : Int,
    val isediting : Boolean = false,

)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Shoppinglistui() {
    var ShoppingItems by remember { mutableStateOf(emptyList<ShoppingItem>()) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Shopping List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item"
                )
            }
        }
    ) { innerpadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {

            LazyColumn() {
                items(ShoppingItems) { item ->
                    if (item.isediting) {
                        EditableShoppingItem(
                            item = item,
                            onEditCompleat = { name, quantity ->
                                ShoppingItems = ShoppingItems.map {
                                    if (it.id == item.id) it.copy(
                                        name = name,
                                        quantity = quantity,
                                        isediting = false
                                    )
                                    else it.copy(isediting = false)
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            oneEdit = {
                                ShoppingItems =
                                    ShoppingItems.map { it.copy(isediting = it.id == item.id) }
                            },
                            onDelete = {
                                ShoppingItems = ShoppingItems.filter { it.id != item.id }
                            }

                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerpadding)
            ) {

                if (showDialog) {
                    AddItemsDialog(
                        itemName = newItemName,
                        itemQuantity = newItemQuantity.toString(),
                        onItemNameChange = { newItemName = it },
                        onItemQuantityChange = { newItemQuantity = it },
                        onDismiss = { showDialog = false },
                        onConfirm = {
                            val quantity = newItemQuantity.toIntOrNull() ?: 1
                            ShoppingItems = ShoppingItems + ShoppingItem(
                                id = ShoppingItems.size + 1,
                                name = newItemName,
                                quantity = quantity
                            )
                            showDialog = false
                            newItemName = ""
                            newItemQuantity = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddItemsDialog(
    itemName :String,
    itemQuantity : String,
    onItemNameChange : (String) -> Unit,
    onItemQuantityChange : (String) -> Unit,
    onDismiss : () -> Unit,
    onConfirm : () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    label = { Text("Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = itemQuantity,
                    onValueChange = onItemQuantityChange,
                    label = { Text("Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}


@Composable
fun ShoppingListItem(
    item :ShoppingItem,
    oneEdit :() -> Unit,
    onDelete : () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.name , style = MaterialTheme.typography.titleMedium)
                Text(text = "Quantity : ${item.quantity}" , style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = oneEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            Row {
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
            }
        }
    }
  }
}


@Composable
fun EditableShoppingItem(
    item : ShoppingItem,
    onEditCompleat : (String , Int) -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedquantity by remember { mutableStateOf(item.quantity.toString())}

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Item Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.fillMaxWidth().padding(8.dp))

            OutlinedTextField(
                value = editedquantity,
                onValueChange = { editedquantity = it },
                label = { Text("Quantity") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val quantity =editedquantity.toIntOrNull() ?: 1
                    onEditCompleat(editedName,quantity)
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
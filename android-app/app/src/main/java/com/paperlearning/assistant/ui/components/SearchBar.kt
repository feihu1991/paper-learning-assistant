package com.paperlearning.assistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索论文标题、作者或关键词..."
) {
    var textFieldValue by remember(query) { mutableStateOf(TextFieldValue(query)) }

    // Update internal state when query changes from outside
    LaunchedEffect(query) {
        textFieldValue = TextFieldValue(query)
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onQueryChange(newValue.text)
        },
        modifier = modifier,
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    textFieldValue = TextFieldValue("")
                    onQueryChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "清除"
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        onKeyEvent = { keyEvent ->
            if (keyEvent.key.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                onSearch()
                true
            } else {
                false
            }
        }
    )
}

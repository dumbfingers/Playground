package com.example.myapplicationnohint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.myapplicationnohint.ui.theme.MyApplicationNoHintTheme
import com.example.myapplicationnohint.viewmodel.MainViewModel
import com.example.myapplicationnohint.viewmodel.MainViewState
import kotlin.random.Random

@Composable
internal fun MainUi(
    viewModel: MainViewModel
) {
    val state by viewModel.state.collectAsState()

    MainUi(viewState = state) {
        viewModel.onArrayUpdated(it)
    }
}

@Composable
private fun MainUi(
    viewState: MainViewState,
    onButtonClick: (ArrayList<Int>) -> Unit
) {
    var text1 by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var text2 by remember {
        mutableStateOf(TextFieldValue(""))
    }
    ProvideTextStyle(value = TextStyle(color = MaterialTheme.colors.primary)) {
        Surface {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(16.dp))
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = text1,
                    label = { Text(text = "Label 1") },
                    placeholder = { Text(text = viewState.placeholder1) },
                    onValueChange = {
                        text1 = it
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = text2,
                    label = { Text(text = viewState.placeholder2, maxLines = 1) },
                    placeholder = { Text(text = viewState.placeholder2, maxLines = 1, modifier = Modifier.padding(end = 16.dp)) },
                    onValueChange = {
                        text2 = it
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = {
                    val random = Random(10)
                    val list = arrayListOf(random.nextInt(), random.nextInt())
                    onButtonClick(list)
                }) {
                    Text(text = "Magic Button")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPlaceholderOverflow1(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    MainUi(
        viewState = MainViewState(
            placeholder1 = text
        )
    ) {}
}

/**
 * If the label and the placeholder are extra long text then the label is omitted even when
 * the text field is in focus. And the place holder is exceeding its bounds
 */

@Preview
@Composable
fun PreviewPlaceholderOverflow2(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    MainUi(
        viewState = MainViewState(
            placeholder2 = text
        )
    ) {}
}
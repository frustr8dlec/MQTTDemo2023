package com.example.mqtt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mqtt.ui.MqttViewModel
import com.example.mqtt.ui.theme.MQTTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MQTTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(modifier: Modifier = Modifier, mqttViewModel: MqttViewModel = viewModel()) {
    val mqttUiState by mqttViewModel.mqttState.collectAsState()
    Column {
        Text(
            text = "MQTT Activity : ${mqttUiState.currentMessage}!",
            modifier = modifier.defaultMinSize(minHeight = 100.dp),

        )
        TextField(value = mqttUiState.sendMessage, onValueChange = mqttViewModel::setMessage )
        Button(onClick = { mqttViewModel.sendMessage(mqttUiState.sendMessage, 0) }) {
            Text(
                text = "Send MQTT Message",
                modifier = modifier
            )

        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MQTTTheme {
        Greeting()
    }
}
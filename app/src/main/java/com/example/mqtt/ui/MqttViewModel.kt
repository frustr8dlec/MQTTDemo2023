package com.example.mqtt.ui


import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mqtt.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.*


class MqttViewModel: ViewModel() {

    private val _mqttState = MutableStateFlow(MqttState())
    val mqttState: StateFlow<MqttState> = _mqttState.asStateFlow()

    private val TAG = "MQTT"

    private val defaultServerUri = "ssl://io.adafruit.com:8883"

    private val subscriptionTopics = arrayOf("frustr8dlec/feeds/samplemessages", "frustr8dlec/feeds/onoff/json")
    private val subscriptionQos = intArrayOf(0, 0)

    // Publish topic
    val publishTopics = arrayOf("frustr8dlec/feeds/samplemessages")

    private val clientId = "MyAndroidClientId${System.currentTimeMillis()}"

    private var mqttManager: MQTTManager

    init {
        var serverUri = defaultServerUri
        val username = ""
        val password = ""

        // Create MQTT Manager
        mqttManager = MQTTManager(
            App.appContext,
            serverUri,
            clientId,
            subscriptionTopics,
            subscriptionQos
        )
        mqttManager.init()
        initMqttStatusListener()

        //displayToast("Attempting connection to MQTT Broker")
        mqttManager.connect(username, password)
    }

    fun sendMessage( message: String,topic: Int) {
        mqttManager.sendMessage(message, publishTopics[topic])
    }


    fun setMessage(message: String){
        _mqttState.update { mqttState ->  mqttState.copy( sendMessage = message)}
    }
    private fun displayInDebugLog(s: String) {
        Log.i(TAG, s)
        _mqttState.update { mqttState -> mqttState.copy(currentMessage = s) }
    }

    private fun initMqttStatusListener() {
        mqttManager.setMqttStatusListener(object : MQTTStatusListener {
            override fun onConnectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    displayInDebugLog("Reconnected to : $serverURI")
                } else {
                    displayInDebugLog("Connected to: $serverURI")
                }
            }

            override fun onConnectionLost(exception: Throwable) {
                displayInDebugLog("The connection was lost.")
            }

            override fun onMessageArrived(topic: String, message: MqttMessage) {
                displayInDebugLog(message.payload.toString(Charsets.UTF_8))
            }

            override fun onTopicSubscriptionSuccess() {
                displayInDebugLog("Subscribed!")
            }

            override fun onTopicSubscriptionError(exception: Throwable) {
                displayInDebugLog("Failed to subscribe")
            }
        })
    }

}
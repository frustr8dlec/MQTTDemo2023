package com.example.mqtt.ui

import org.eclipse.paho.client.mqttv3.MqttMessage

interface MQTTStatusListener {
    fun onConnectComplete(reconnect: Boolean, serverUri: String)
    //    fun onConnectFailure(cause: Throwable)
    fun onConnectionLost(cause: Throwable)
    fun onTopicSubscriptionSuccess()
    fun onTopicSubscriptionError(cause: Throwable)
    fun onMessageArrived(topic: String, message: MqttMessage)
}

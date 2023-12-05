package com.example.mqtt.ui

import android.content.Context
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets


class MQTTManager(
    private val applicationContext: Context,
    private val serverUri: String,
    private val clientId: String,
    private val topics: Array<String>,
    private val topicQos: IntArray
) {

    private var mqttAndroidClient: MqttAndroidClient? = null
    private var mqttStatusListener: MQTTStatusListener? = null

    fun init() {
        mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId, Ack.AUTO_ACK)

        mqttAndroidClient?.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                mqttStatusListener?.onConnectComplete(reconnect, serverURI)
                if (reconnect) {
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic()
                }
            }

            override fun connectionLost(cause: Throwable) {
                mqttStatusListener?.onConnectionLost(cause)
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                mqttStatusListener?.onMessageArrived(topic, message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })
    }

    fun connect(user: String, password: String) {
        mqttAndroidClient?.connect(
            createConnectOptions(user, password),
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    mqttStatusListener?.onTopicSubscriptionSuccess()
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    mqttStatusListener?.onTopicSubscriptionError(exception)
                }
            })
    }

    private fun createConnectOptions(user: String, password: String): MqttConnectOptions {
        val mqttOpts = MqttConnectOptions()
        mqttOpts.isAutomaticReconnect = true
        mqttOpts.isCleanSession = false
        mqttOpts.userName = user
        mqttOpts.password = password.toCharArray()
        return mqttOpts
    }



    private fun subscribeToTopic() {
        mqttAndroidClient?.subscribe(
            topics, topicQos, null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    mqttStatusListener?.onTopicSubscriptionSuccess()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    mqttStatusListener?.onTopicSubscriptionError(exception)
                }
            })
    }

    fun sendMessage(message: String, topic: String) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = message.toByteArray(StandardCharsets.UTF_8)
        mqttAndroidClient?.publish(topic, mqttMessage)
    }

    // Setters
    fun setMqttStatusListener(listener: MQTTStatusListener) {
        mqttStatusListener = listener
    }

}
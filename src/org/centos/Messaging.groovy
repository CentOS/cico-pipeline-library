#!/usr/bin/groovy
package org.centos

/**
 * Library to publish a message using the JMS Messaging Plugin
 * Required Plugin: https://wiki.jenkins.io/display/JENKINS/JMS+Messaging+Plugin
 *
 * Pass a map to the library
 * msgMap requires passing key/values for msgMap.msgProps and msgMap.topic
 * msgMap defaults:
 *  msgMap[msgType:'Custom'
 */
def sendMessage(msgMap) {
    try {
        if ( (!(msgMap.containsKey('msgProps')) || (msgMap.msgProps == ""))
                || (!(msgMap.containsKey('topic')) || (msgMap.topic == "")) ) {
            echo "Message Topic or Message Properties not defined or empty"
            sh 'exit 1'
        }
        sendCIMessage messageContent: msgMap.msgContent,
                messageProperties: msgMap.msgProps,
                messageType: "${msgMap.containsKey('msgType') ? msgMap.msgType : 'Custom'}",
                overrides: [topic: "${msgMap.topic}"],
                providerName: "${msgMap.provider}"
    } catch (err) {
        echo err.getMessage()
        throw err
    }
}
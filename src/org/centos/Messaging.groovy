#!/usr/bin/groovy
package org.centos

/**
 * Library to publish a message using the JMS Messaging Plugin
 *
 * Required Plugin: https://wiki.jenkins.io/display/JENKINS/JMS+Messaging+Plugin
 * @param msgMap Requires passing key/values for msgMap.msgProps and msgMap.topic. Default value is msgMap[msgType:'Custom']
 * @return
 */
def sendMessage(Map msgMap) {
    try {
        if ( (!(msgMap.containsKey('msgProps')) || (msgMap.msgProps == ""))
                || (!(msgMap.containsKey('topic')) || (msgMap.topic == "")) ) {
            echo "Message Topic or Message Properties not defined or empty"
            sh script: 'exit 1', label: "Message Topic or Message Properties not defined or empty"
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

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this
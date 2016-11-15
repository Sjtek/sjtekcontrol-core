#!groovy

node {

    stage('Checkout') {
        checkout scm
    }
    
    stage('Build') {
    	sh './gradlew jar'
    	archive 'core/build/libs/core.jar,data/build/libs/data.jar'
    }

    stage('Test') {
    	sh './gradlew :data:test'
    	junit 'data/build/test-results/*.xml'
    }

    stage('Javadocs') {
        sh './gradlew :data:javadoc'
    }
}

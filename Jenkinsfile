#!groovy

node {

    stage 'Checkout'
    git url: 'https://github.com/Sjtek/sjtekcontrol-core'

    stage 'Build'
    sh './gradlew jar'
    archive 'core/build/libs/core.jar,data/build/libs/data.jar'
}

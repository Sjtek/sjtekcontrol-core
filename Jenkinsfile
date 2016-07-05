#!groovy

node {

    stage 'Checkout'
    git url: 'https://github.com/Sjtek/sjtekcontrol-core'

    stage 'Build'
    sh './gradlew jar'
    archive 'core/build/libs/core.jar,data/build/libs/data.jar'

    stage 'Docker'
    sh '''DATE=`date +%Y%m%d`
          docker build -t whhoesj/sjtekcontrol:latest .
          docker build -t whhoesj/sjtekcontrol:$DATE .
          docker push whhoesj/sjtekcontrol:latest
          docker push whhoesj/sjtekcontrol:$DATE
          docker rmi whhoesj/sjtekcontrol:latest
          docker rmi whhoesj/sjtekcontrol:$DATE'''

}

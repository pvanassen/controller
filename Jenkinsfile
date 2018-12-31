pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }
    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout code') {
            steps {
                checkout scm
            }
        }

        stage ('Compile') {
            steps {
                sh 'mvn -B clean package -Dmaven.test.skip=true -Dbuild=production'
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        stage ('Test') {
            steps {
                sh 'mvn -B test -Dmaven.test.failure.ignore=true'
            }
        }

        stage ('Sonar') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }


        stage ('Deploy snapshot') {
            when {
                not {
                    branch('master')
                }
            }
            parallel {
                stage ('Deploy snapshot') {
                    steps {
                        sh "mvn deploy -DaltDeploymentRepository=$SNAPSHOT_REPOSITORY -Dbuild=production"
                    }
                }
                stage ('Docker snapshot') {
                    steps {
                        script {
                            def pom = readMavenPom file: 'pom.xml'
                            def name = pom.artifactId
                            def image = docker.build "$DOCKER_REPO/$name:snapshot"
                            image.push()
                        }
                    }
                }
            }
        }
        stage ('Deploy master') {
            when {
                branch('master')
            }
            parallel {
                stage ('Deploy release') {
                    steps {
                        sh "mvn deploy -DaltDeploymentRepository=$RELEASE_REPOSITORY -Dbuild=production"
                    }
                }
                stage ('Docker latest') {
                    steps {
                        script {
                            def pom = readMavenPom file: 'pom.xml'
                            def name = pom.artifactId
                            def image = docker.build "$DOCKER_REPO/$name:latest"
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
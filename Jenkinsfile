pipeline {
    agent any
    environment {
        DOCKER_IMAGE_TAG = "17-${env.BUILD_ID}"
        MVN_OPTS = "-B -Dmaven.repo.local=$WORKSPACE/.m2/repository"
    }
    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }
    stages {

        stage('Checkout') {
            steps {
                ansiColor('xterm') {
                    checkout scm
                }
            }
        }

        stage('Build & Package Services') {
            parallel {
                stage('Product Service') {
                    steps {
                        ansiColor('xterm') {
                            dir('product-service') {
                                sh 'chmod +x mvnw'
                                sh "./mvnw clean package -DskipTests $MVN_OPTS"
                            }
                        }
                    }
                }
                stage('Order Service') {
                    steps {
                        ansiColor('xterm') {
                            dir('order-service') {
                                sh 'chmod +x mvnw'
                                sh "./mvnw clean package -DskipTests $MVN_OPTS"
                            }
                        }
                    }
                }
                stage('API Gateway') {
                    steps {
                        ansiColor('xterm') {
                            dir('api-gateway') {
                                sh 'chmod +x mvnw'
                                sh "./mvnw clean package -DskipTests $MVN_OPTS"
                            }
                        }
                    }
                }
            }
        }

        stage('Docker Build Images') {
            steps {
                ansiColor('xterm') {
                    sh '''
                    docker-compose -f docker-compose.yml build --parallel
                    '''
                }
            }
        }

        stage('Push Docker Images') {
            when {
                expression { return env.BRANCH_NAME == 'main' }
            }
            steps {
                ansiColor('xterm') {
                    script {
                        sh 'echo $DOCKER_HUB_PASS | docker login -u $DOCKER_HUB_USER --password-stdin'
                        sh 'docker-compose -f docker-compose.yml push || true'
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when {
                expression { return env.BRANCH_NAME == 'main' }
            }
            steps {
                ansiColor('xterm') {
                    sh 'kubectl apply -f k8s/'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            cleanWs()
        }
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed. Check the logs!"
        }
    }
}
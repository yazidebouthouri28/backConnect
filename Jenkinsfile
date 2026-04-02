def SERVICES = [
    [name: 'product-service', deployment: 'deployment/product-service'],
    [name: 'order-service',   deployment: 'deployment/order-service'],
    [name: 'api-gateway',     deployment: 'deployment/api-gateway'],
     [name: 'user-service',    deployment: 'deployment/user-service']
]

pipeline {
    agent any

    environment {
        DOCKER_REPO         = "azizbenabdallah/ecommerceback"
        DOCKER_USER         = "azizbenabdallah"
        DOCKER_PASS         = "jc-i5jxUL\$H36N4"
        DOCKER_IMAGE_TAG    = "${BUILD_NUMBER}"
        MVN_OPTS            = "-T 1C -B -ntp -Dmaven.repo.local=${WORKSPACE}/.m2/repository"
        K8S_ROLLOUT_TIMEOUT = "180s"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        skipStagesAfterUnstable()
    }

    stages {

        // ── 1. Checkout shallow ─────────────────────────────────
        stage('Checkout') {
            steps {
                retry(3) {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/AzizBack']],
                        extensions: [
                            [$class: 'CloneOption',
                                depth: 1,
                                shallow: true,
                                noTags: true,
                                timeout: 60
                            ],
                            [$class: 'CleanBeforeCheckout']
                        ],
                        userRemoteConfigs: [[
                            url: 'https://github.com/yazidebouthouri28/Esprit-PI-4SE3-2025-2026-ConnectCamp-Backend.git'
                        ]]
                    ])
                }
            }
        }

        // ── 2. Build Maven parallèle ────────────────────────────
        stage('Build & Package') {
            steps {
                script {
                    parallel SERVICES.collectEntries { svc ->
                        def service = svc
                        ["Maven › ${service.name}": {
                            timeout(time: 10, unit: 'MINUTES') {
                                dir(service.name) {
                                    sh 'chmod +x mvnw'
                                    sh "./mvnw clean package -DskipTests ${MVN_OPTS}"
                                }
                            }
                        }]
                    }
                }
            }
        }

        // ── 3. Docker Build & Push parallèle ───────────────────
        stage('Docker Build & Push') {
            when { branch 'AzizBack' }
            steps {
                script {
                    sh "docker login -u ${DOCKER_USER} -p '${DOCKER_PASS}'"

                    parallel SERVICES.collectEntries { svc ->
                        def service = svc
                        ["Docker › ${service.name}": {
                            timeout(time: 10, unit: 'MINUTES') {
                                sh """
                                    docker build \
                                        -t ${DOCKER_REPO}-${service.name}:${DOCKER_IMAGE_TAG} \
                                        -t ${DOCKER_REPO}-${service.name}:latest \
                                        ./${service.name}
                                """
                                retry(3) {
                                    sh """
                                        docker push ${DOCKER_REPO}-${service.name}:${DOCKER_IMAGE_TAG}
                                        docker push ${DOCKER_REPO}-${service.name}:latest
                                    """
                                }
                            }
                        }]
                    }

                    sh 'docker logout || true'
                }
            }
        }

        // ── 4. Deploy Kubernetes ────────────────────────────────
        stage('Deploy to Kubernetes') {
            when { branch 'AzizBack' }
            steps {
                script {
                    sh 'kubectl apply -f k8s/'

                    parallel SERVICES.collectEntries { svc ->
                        def service = svc
                        ["Rollout › ${service.name}": {
                            timeout(time: 5, unit: 'MINUTES') {
                                try {
                                    sh "kubectl rollout status ${service.deployment} --timeout=${K8S_ROLLOUT_TIMEOUT}"
                                } catch (err) {
                                    echo "⚠️ Rollout échoué pour ${service.name}"
                                    sh "kubectl rollout undo ${service.deployment}"
                                    error("Rollback déclenché : ${service.name}")
                                }
                            }
                        }]
                    }
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
            echo "✅ Build ${BUILD_NUMBER} terminé → tag: ${DOCKER_IMAGE_TAG}"
        }
        failure {
            echo "❌ Build ${BUILD_NUMBER} échoué"
        }
    }
}
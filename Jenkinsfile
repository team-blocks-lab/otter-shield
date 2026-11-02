pipeline {
    agent {
        label 'm3-builder'
    }

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        IMAGE_REPO = 'docker.io/eunseo03170/otter-ott-api'
        CONTAINER_NAME = 'ott-api'
    }

    stages {
        stage('1. GitHub Checkout') {
            steps {
                checkout scm

                script {
                    env.SHORT_SHA = sh(
                        script: 'git rev-parse --short=12 HEAD',
                        returnStdout: true
                    ).trim()

                    env.IMAGE = "${env.IMAGE_REPO}:${env.SHORT_SHA}"
                }

                echo "배포 이미지: ${env.IMAGE}"
            }
        }

        stage('2. Spring Boot Image Build') {
            steps {
                sh '''
                    set -eu

                    podman build \
                      -f apps/m3-ott/backend/Dockerfile \
                      -t "$IMAGE" \
                      apps/m3-ott/backend
                '''
            }
        }

        stage('3. Docker Hub Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-ott',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_TOKEN'
                    )
                ]) {
                    sh '''
                        set -eu
                        set +x

                        AUTH_DIR=$(mktemp -d)
                        trap 'rm -rf "$AUTH_DIR"' EXIT

                        printf '%s' "$DH_TOKEN" | \
                          podman login docker.io \
                          -u "$DH_USER" \
                          --password-stdin \
                          --authfile "$AUTH_DIR/auth.json"

                        podman push \
                          --authfile "$AUTH_DIR/auth.json" \
                          "$IMAGE"
                    '''
                }
            }
        }

        stage('4. Kubernetes Deploy') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'm3-kubeconfig',
                        variable: 'KCFG'
                    )
                ]) {
                    sh '''
                        set -eu

                        PATCH=$(printf \
                          '{"spec":{"template":{"spec":{"containers":[{"name":"%s","image":"%s","imagePullPolicy":"Always"}]}}}}' \
                          "$CONTAINER_NAME" "$IMAGE")

                        kubectl --kubeconfig="$KCFG" \
                          -n ott-app patch \
                          deployment/otter-ott-real \
                          --type=strategic \
                          -p "$PATCH"

                        kubectl --kubeconfig="$KCFG" \
                          -n ott-app rollout status \
                          deployment/otter-ott-real \
                          --timeout=300s
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Spring Boot Kubernetes 배포 성공'
        }

        failure {
            echo '배포 실패 - Console Output 확인'
        }
    }
}


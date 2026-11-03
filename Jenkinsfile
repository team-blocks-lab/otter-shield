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
                    
        stage('5. React Build') {
            steps {
                sh '''
                    set -eu
        
                    podman run --rm \
                      --userns=keep-id \
                      -v "$PWD/apps/m3-ott/frontend:/app:Z" \
                      -w /app \
                      -e VITE_USE_MOCK=false \
                      -e VITE_LOCAL_DEMO_PLAYBACK=false \
                      -e VITE_API_BASE_URL= \
                      -e VITE_MEDIA_BASE_URL=https://dcx0apovwtx2p.cloudfront.net \
                      docker.io/library/node:22 \
                      sh -c 'npm ci && npm run build'
        
                    test -f apps/m3-ott/frontend/dist/index.html
        
                    tar -czf "otter-react-${SHORT_SHA}.tar.gz" \
                      -C apps/m3-ott/frontend/dist .
                '''
            }
        }
        
      
        stage('6. React Deploy to Nginx') {
            steps {
                sshagent(credentials: ['web-deploy-ssh']) {
                    sh '''
                        set -eu
        
                        FILE="otter-react-${SHORT_SHA}.tar.gz"
                        WEB="webdeploy@192.168.110.10"
                        RELEASE="/var/www/otter-ui/releases/${SHORT_SHA}"
        
                        scp -o StrictHostKeyChecking=yes \
                          "$FILE" "$WEB:/home/webdeploy/$FILE"
        
                        ssh -o StrictHostKeyChecking=yes "$WEB" \
                          "mkdir -p '$RELEASE' &&
                           tar -xzf '/home/webdeploy/$FILE' -C '$RELEASE' &&
                           test -s '$RELEASE/index.html' &&
                           ln -sfn '$RELEASE' '/var/www/otter-ui/.next-${SHORT_SHA}' &&
                           mv -Tf '/var/www/otter-ui/.next-${SHORT_SHA}' '/var/www/otter-ui/current' &&
                           rm -f '/home/webdeploy/$FILE'"
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


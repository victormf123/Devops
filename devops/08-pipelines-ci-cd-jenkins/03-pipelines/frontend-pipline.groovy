podTemplate(
    name: 'questcode',
    namespace: 'devops', 
    label: 'questcode',
    containers: [
            containerTemplate(args: 'cat', command: '/bin/sh -c', image: 'docker', livenessProbe: containerLivenessProbe(execArgs: '', failureThreshold: 0, initialDelaySeconds: 0, periodSeconds: 0, successThreshold: 0, timeoutSeconds: 0), name: 'docker-container', resourceLimitCpu: '', resourceLimitMemory: '', resourceRequestCpu: '', resourceRequestMemory: '', ttyEnabled: true),
            containerTemplate(args: 'cat', command: '/bin/sh -c', image: 'lachlanevenson/k8s-helm:v2.11.0', name: 'helm-container', ttyEnabled: true)
        ],
    volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')], 
)
{
    //  Start Pipeline
    node('questcode') {
        def REPOS
        def IMAGE_VERSION
        def IMAGE_NAME =  "questcode-frontend"
        def ENVIROMENT = "staging"
        def GIT_REPOS_URL = "git@gitlab.com:victormf.df/questcode-frontend.git"
        def CHARTMUSEUM_URL = "http://helm-chartmuseum:8080"
        stage('Checkout') {
            echo 'Iniciando Clone do Repositorio'
            REPOS = git credentialsId: 'gitlab', url: GIT_REPOS_URL
            IMAGE_VERSION = sh label: '', returnStdout: true, script: 'sh read-package-version.sh'
            IMAGE_VERSION = IMAGE_VERSION.trim()
        }
        stage('Package') {
            container('docker-container') {
                echo 'Iniciando empacotamento com Docker'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_HUB_PASSWORD', usernameVariable: 'DOCKER_HUB_USER')]) {
                    sh label: '', script: "docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}"
                    sh label: '', script: "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_VERSION} . --build-arg NPM_ENV='${ENVIROMENT}'"
                    sh label: '', script: "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_VERSION}"
        
                }
                
            }
        }
        stage('Deploy') {
            // CLIENT DO HELM
            container('helm-container') {
                echo 'Iniciando Deploy com Helm'
                sh label: '', script: """
                                        helm init --client-only
                                        helm repo add questcode ${CHARTMUSEUM_URL}
                                        helm repo update
                                        helm upgrade staging-frontend questcode/frontend --set image.tag=${IMAGE_VERSION}
                                      """
                
            }
        }
    }// end of node
}
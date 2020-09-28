#!/bash/sh

kubectl apply -f 01-jenkins-pv-pvc.yaml

helm install --name jenkins --set persistence.existingClaim=jenkins --set master.serviceType=NodePort --set master.nodePort=30808 --set master.tag=2.235.1-lts-jdk11 --namespace devops stable/jenkins
# helm install --name jenkins --set persistence.existingClaim=jenkins --set master.serviceType=NodePort --set master.nodePort=30808 --namespace devops stable/jenkins

kubectl create rolebinding sa-devops-role-clusteradmin --clusterrole=cluster-admin --serviceaccount=devops:default --namespace=devops

kubectl create rolebinding sa-devops-role-clusteradmin-kubesystem --clusterrole=cluster-admin --serviceaccount=devops:default --namespace=kube-system
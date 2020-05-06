#!/bash/sh
helm install --name nfs-provisioner --namespace devops --set nfs.server=192.168.1.175 --set nfs.path=/data/Kubernetes --set storageClass.defaultClass=true stable/nfs-client-provisioner
helm install --name postgres --namespace devops --set persistence.existingClaim=postgres-pv-claim stable/postgresql
#!/bin/sh
curl -L https://git.io/get_helm.sh | bash
kubectl apply -f 01-tiller-account.yaml

helm init --service-account tiller
#helm init
#kubectl patch deployment tiller-deploy -n kube-system --patch "$(cat 01-tiller-patch.yaml)"
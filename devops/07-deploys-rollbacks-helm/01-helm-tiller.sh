#!/bin/sh
curl -L https://git.io/get_helm.sh | bash
kubectl apply -f 01-tiller-account.yaml

helm init --service-account tiller


#helm init --service-account tiller --output yaml | sed 's@apiVersion: extensions/v1beta1@apiVersion: apps/v1@' | sed 's@  replicas: 1@  replicas: 1\n  selector: {"matchLabels": {"app": "helm", "name": "tiller"}}@' | kubectl apply -f -
#helm init
#kubectl patch deployment tiller-deploy -n kube-system --patch "$(cat 01-tiller-patch.yaml)"
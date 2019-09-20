#!/bash/sh

helm lint 02-charts/backend-scm/
helm push 02-charts/backend-scm/ questcode

helm lint 02-charts/backend-user/
helm push 02-charts/backend-user/ questcode

helm lint 02-charts/frontend/
helm push 02-charts/frontend/ questcode

helm repo update
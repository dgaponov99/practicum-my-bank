helm repo add external-secrets https://charts.external-secrets.io
helm repo update
helm install external-secrets external-secrets/external-secrets -n external-secrets --create-namespace
kubectl create secret generic vault-token --from-literal=token=66b52c97-e8f6-445e-8ebb-1a5f4d443d7e

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace

helm repo add kafka-repo https://helm-charts.itboon.top/kafka
helm repo update
helm install kafka kafka-repo/kafka -n kafka --create-namespace --set broker.combinedMode.enabled=true --set broker.replicaCount=3 --set broker.config.min.insync.replicas=2
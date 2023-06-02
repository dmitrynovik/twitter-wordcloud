NAMESPACE="tanzu-gemfire"
TARGET_FOLDER="./gemfire-certs"

kubectl get secret -n $NAMESPACE gemfire-cluster-cert -o=jsonpath='{.data.password}' | base64 --decode > "$TARGET_FOLDER/password"
kubectl get secret -n $NAMESPACE gemfire-cluster-cert -o=jsonpath='{.data.keystore\.p12}' | base64 --decode > "$TARGET_FOLDER/keystore.p12"
kubectl get secret -n $NAMESPACE gemfire-cluster-cert -o=jsonpath='{.data.truststore\.p12}' | base64 --decode > "$TARGET_FOLDER/truststore.p12"

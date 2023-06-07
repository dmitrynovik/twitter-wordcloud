kubectl -n tanzu-gemfire exec -it gemfire-cluster-locator-0 -- gfsh

# type in gfsh:
connect --locator=gemfire-cluster-locator-0.gemfire-cluster-locator.tanzu-gemfire.svc.cluster.local[10334] --security-properties-file=/security/gfsecurity.properties
create region --name=/Tweets --type=REPLICATE

# Introduction 
This is a Java Spring Boot micro service application which exposes a REST API against an in memory MognoDB instance.  Intended to be deployed to Azure Kubernetes Services this application is design to demonstrate the features for the [Java Applications Insights Codeless jar](https://docs.microsoft.com/en-us/azure/azure-monitor/app/java-in-process-agent) file.

# Getting Started
To use this application you will need to do the following
- Required software
    - [Docker desktop](https://www.docker.com/products/docker-desktop)
    - kubectl which will be installed with Docker Desktop
    - [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
- Generate a SAS token from the Azure container blob which has the private preview jar file.
- Update the [docker file](./source/Dockerfile) on line 10 to point to the newly created url including SAS token.
- Deploy an Azure Kubernetes Service to a subscription that you have access to. See: https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough-portal#create-an-aks-cluster
- [Get admin credentials from AKS](https://docs.microsoft.com/en-us/cli/azure/aks?view=azure-cli-latest#az_aks_get_credentials)
- Create an Azure Container Registry
- Ensure you can push and pull images to your new ACR.  See: https://docs.microsoft.com/en-us/azure/container-registry/container-registry-get-started-docker-cli?tabs=azure-cli
- Grant your AKS cluster access to pull images from your ACR.  See: https://docs.microsoft.com/en-us/azure/aks/cluster-container-registry-integration
- Do a docker build in the same directory as the [docker file](./source/Dockerfile).
- Tag the newly created docker image with the appropriate name from your ACR
  - Example: docker tag cf2afeb88816 dracr.azurecr.io/cosmos-demo:latest
- Push the image
- Update jfr-deploy.yml file with the correct docker image name.
- If everything worked you should be able to do a kubectl apply -f ./jfr-deploy.yml and have a working site.


# Example Commands I used

az acr create --name DrAcr --resource-group aks-westus --sku Standard
az aks update --name draks  --resource-group aks-westus --attach-acr DrAcr
az aks check-acr --name draks --resource-group aks-westus --acr DrAcr.azurecr.io
az acr update --admin-enabled true --name dracr
az acr login --name dracr
# before building the docker image you need to generate a sas token for the jar file
Docker build .
docker tag cf2afeb88816 dracr.azurecr.io/cosmos-demo:latest
docker push dracr.azurecr.io/cosmos-demo:latest


# gridwalls2
Gridwalls - the game

What's more fun than learning stuff by implementing a game?

Current plan:
- Make a cool game
- Use microservices, docker and other stuff

## Installation

* Start a Kubernetes cluster (use kubeadm)
  * Set up a default storage class
  	`kube-apiserver --admission-control=DefaultStorageClass`

### Install Helm

From: https://github.com/kubernetes/helm/blob/master/docs/rbac.md

```
kubectl create namespace gw
kubectl create serviceaccount tiller --namespace gw
kubectl create clusterrolebinding tiller --clusterrole=cluster-admin --serviceaccount=gw:tiller
helm init --service-account tiller --tiller-namespace=gw
echo "export TILLER_NAMESPACE=gw" >> ~/.bashrc
```

### Install private docker registry

```
helm install stable/docker-registry
POD_NAME=$(kubectl get pods --namespace gw -l "app=docker-registry,release=bold-cat" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward $POD_NAME 5000
```

### Install default storage provider, Heketi+GlusterFS

#### Setup DNS

Here we have 1 master and 1 slave.

On master host:

sudo echo "<node ip> gluster2" >> /etc/hosts # replace with node ip

On slave host:

sudo echo "<master ip> gluster1" >> /etc/hosts # replace with master ip

#### Use master as NTP source

Source: https://serverfault.com/questions/204082/using-ntp-to-sync-a-group-of-linux-servers-to-a-common-time-source



#### Install GlusterFS

Follow instructions here: http://gluster-documentations.readthedocs.io/en/master/Install-Guide/Quick_start/

Currently, that means doing (with a few deviations because no docs are pertfect):
```
apt update -y && apt upgrade -y

fdisk /dev/vdb # and create a single partition with n, e(xtended), the rest are deafult settings. Then w(rite and quit)
apt install -y xfsprogs # To install mkfs.xfs
mkfs.xfs -f -i size=512 /dev/vdb
mkdir -p /export/vdb && mount /dev/vdb /export/vdb && mkdir -p /export/vdb/brick
echo "/dev/vdb /export/vdb xfs defaults 0 0"  >> /etc/fstab
```

From http://gluster-documentations.readthedocs.io/en/master/Install-Guide/Install/:

```
apt install -y software-properties-common # To make add-apt-repository work
apt install -y python-software-properties
add-apt-repository ppa:gluster/glusterfs-4.0
apt-get update -y
apt install -y glusterfs-server
```



### Run the game

* (Opt) Watch pods: `watch kubectl get po`
* (Opt) Watch helm releases: `watch helm list`
* `export GR_DOCKER_REGISTRY=localhost:5000/`
* Run rabbitmq
  * `make -C source/microservices/rabbitmq ks-build ks-run`
* Run helm repo
  * `make -C source/lib/helm_charts package host`
* Run map-info-producer
  * `make -C source/microservices/map_info_producer ks ks-run`
  * (Opt) Show logs: `kubectl logs -f --namespace default $(kubectl get po -l "app=gridwalls-microservice" -l "release=map-info-producer" -o jsonpath="{.items[0].metadata.name}") netcom-forwarder`
  * `make -C source/microservices/zombie-light ks ks-run`
  * (Opt) Show logs: `kubectl logs -f --namespace default $(kubectl get po -l "app=gridwalls-microservice" -l "release=zombie-light" -o jsonpath="{.items[0].metadata.name}") netcom-forwarder`
* Run front-end
  * TODO

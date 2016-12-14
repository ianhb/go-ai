#/bin/bash

command -v java >/dev/null 2>&1 || { echo >&2 "I require java 8 but it's not installed.  Aborting."; exit 1; }
command -v gradle >/dev/null 2>&1 || { echo >&2 "I require virtualenv but it's not installed.  Aborting."; exit 1; }
command -v virtualenv >/dev/null 2>&1 || { echo >&2 "I require virtualenv but it's not installed.  Aborting."; exit 1; }
command -v pip >/dev/null 2>&1 || { echo >&2 "I require pip but it's not installed.  Aborting."; exit 1; }
command -v git >/dev/null 2>&1 || { echo >&2 "I require git but it's not installed.  Aborting."; exit 1; }

echo "Setting Up Python Environment"

if [ ! -r venv ]; then
virtualenv venv

source venv/bin/activate
cd python
pip install -r REQUIREMENTS.txt
python -m grpc.tools.protoc -I../proto --python_out=generated --grpc_python_out=generated ../proto/*.proto
export TF_BINARY_URL=https://storage.googleapis.com/tensorflow/linux/cpu/tensorflow-0.11.0-cp27-none-linux_x86_64.whl
pip install --upgrade $TF_BINARY_URL
cd ..
deactivate
fi

echo "Setting Up TensorFlow Server"
if [ ! -r serving ]; then
	wget https://github.com/bazelbuild/bazel/releases/download/0.4.1/bazel-0.4.1-jdk7-installer-linux-x86_64.sh
	chmod +x bazel-0.4.1-jdk7-installer-linux-x86_64.sh
	./bazel-0.4.1-jdk7-installer-linux-x86_64.sh --user
	git clone --recurse-submodules https://github.com/tensorflow/serving
	cd serving/tensorflow
	./configure
	cd ..
	bazel build tensorflow_serving/model_servers:tensorflow_model_server
	cd ..
fi


echo "Installing Dependencies"
sudo apt-get update && sudo apt-get install -y \
        build-essential \
        curl \
        libcurl3-dev \
        git \
        libfreetype6-dev \
        libpng12-0 \
        libzmq3-dev \
        pkg-config \
        python-dev \
        python-numpy \
        python-pip \
        software-properties-common \
        swig \
        zip \
        zlib1g-dev


echo "Creating models"
source venv/bin/activate
cd python
if [ ! -r datagen/data/fuseki-TRAIN.tfrecords ]; then
	cd datagen
	python create_records.py
	cd ..
fi
python trainer.py --model_type=fast
mkdir /tmp/models
cp -r models/export /tmp/models



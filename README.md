# GoAI: Beta Go
#### Ian Blakley
#### Alpha Go Reimplementation

### Tech

Beta Go relies on several components to work correctly:

* Java 8
* Python 2.7
* Gradle 3.1
* JavaFX 


### Installation

Make sure the tech requirements (Java 8, Python 2.7 and Gradle 3.1) are installed

Clone the repository and download the game records from the lastest release to python/datagen/data

Setting up the python environment:
```sh
$ virtualenv goaivenv
$ source goaivenv/bin/activate
$ cd goai/python
$ pip install -r requirements.txt
$ python -m grpc.tools.protoc -I../proto --python_out=generated --grpc_python_out=generated ../proto/*.proto
```
Since TensorFlow isn't in PyPi, it has to be installed manually. Instruction to install the lastest version 
can are [here](https://www.tensorflow.org/versions/r0.11/get_started/os_setup.html)

TensorFlow serving requires a C++ server to allow querying the neural network. In order to setup
the server, follow the installation instructions [here](https://tensorflow.github.io/serving/setup)

Briefly, install [Bazel](https://github.com/bazelbuild/bazel/releases) and execute 
```sh 
$ cd ~/Downloads
$ chmod +x bazel-0.3.2-installer-linux-x86_64.sh
$ ./bazel-0.3.2-installer-linux-x86_64.sh --user
```
and add ``$HOME/bin`` to your path. Install the following packages:

```sh 
$ sudo apt-get update && sudo apt-get install -y \
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
```

Generating data and training the model:
```sh
$ cd goai/python/datagen
$ python create_records.py
$ cd ..
$ python trainer.py --model_type=fast
$ mkdir /tmp/models
$ cp -r models/export /tmp/models
```
The whole process takes ~1 day to generate the data and train on it

Start Game Logger Server:
```sh
$ cd goai/python
$ python main.py
```

To build and start the neural network server
```sh
$ git clone --recurse-submodules https://github.com/tensorflow/serving
$ cd serving
$ cd serving/tensorflow
$ ./configure
$ cd ..
$ bazel build tensorflow_serving/model_servers:tensorflow_model_server
$ ./bazel-bin/tensorflow_serving/model_servers/tensorflow_model_server --model_name=fast --model_base_path=/tmp/models/export/fast_model
```

Run all possible games:
```sh
$ cd goai/java
$ gradle :clean :build :run
```

### Current Bots

* **Random Bot:** Randomly selects a legal move to play
* **Random MCTS Bot:** Uses the Monte Carlo Tree Search algorithm with random expansion
* **UCT Bot:** Uses the Monte Carlo Tree Search algorithm with Upper Confidence Threshold expansion
* **Neural Network Bot:** Uses a trained neural network to select move given the current board state
* **Human Bot:** Takes console input to make a move
* **Alpha Go Bot:** Uses a combination of neural networks and MCTS to select move
* **Pseudo-Alpha Bot:** Uses the Alpha Go Bot tree policy and random default policy

### Acknowledgements

* [JavaFX-Gradle-Plugin](https://github.com/FibreFoX/javafx-gradle-plugin)





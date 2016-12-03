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

Generating data and training the model:
```sh
$ cd goai/python/datagen
$ python create_records.py
$ cd ..
$ python input_data_reader.py
```
The whole process takes ~1 day to generate the data and train on it

Start Neural Network Server:
```sh
$ cd goai/python
$ python main.py
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

### Acknowledgements

* (JavaFX-Gradle-Plugin)[https://github.com/FibreFoX/javafx-gradle-plugin]





nohup ./serving/bazel-bin/tensorflow_serving/model_servers/tensorflow_model_server --model_name=fast --model_base_path=/tmp/models/export/fast_model --port=9000 &
nohup ./serving/bazel-bin/tensorflow_serving/model_servers/tensorflow_model_server --model_name=fast --model_base_path=/tmp/models/export/fast_model --port=9001

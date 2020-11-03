TransferTool

##### The transfer tools currently support the following features:
###### Receiver:

| title | Configuration class name | Description |
| ------------- | ------------- | ------------- |
| Fs            | ReceiverConfigFs      | Read a message from a disk file    |
| Ftp           | ReceiverConfigFtp     | Receive messages using the Ftp/Ftps protocol    |
| Http          | ReceiverConfigHttp    | Receive messages using the http/https protocol (supports pull mode and provides the Restfull interface mode) |
| Ftp           | ReceiverConfigFtp     | Receive messages using the Ftp/Ftps protocol   |
| SFtp          | ReceiverConfigSftp    | Receive messages using the SFtp protocol       |
| Email         | ReceiverConfigEmail   | Receive messages using the Email protocol          |
| Jms           | ReceiverConfigJms     | Receive messages using the Jms protocol   |
| Kafka         | ReceiverConfigKafka   | Receive messages using the Kafka protocol     |
| IbmMq         | ReceiverConfigIbmMq   | Receive messages using the IbmMq protocol     |
| RabbitMq      | ReceiverConfigRabbitMq| Receive messages using the RabbitMq protocol     |
| RocketMq      | ReceiverConfigRocketMq| Receive messages using the RocketMq protocol     |
| ActiveMq      | ReceiverConfigActiveMq| Receive messages using the ActiveMq protocol     |
| Hdfs          | ReceiverConfigHdfs    | Receive messages using the HDFS protocol     |

Filter：

| title | Configuration class name | Description |
| --------      | -----                    | :----  |
| Backup        | FilterConfigBackup        | Back up messages to the file system   |
| Compress      | FilterConfigCompress      | Compress the message  |
| Decompress    | FilterConfigDecompress    | Decompress the message  |
| EncryptDecrypt| FilterConfigEncryptDecrypt| Encrypt and decrypt the message   |
| OracleSqlldr  | FilterConfigOracleSqlldr  | Save the message to the Oracle database   |
| GroovyScript  | FilterConfigGroovyScript  | Will execute the Groovy script    |
| PythonScript  | FilterConfigPythonScript  | Will execute Python script         |
| JavaScript    | FilterConfigJavaScript    | Will execute a JavaScript script   |
| LuaScript     | FilterConfigLuaScript     | Will execute the Lua script        |
| UnicodeTransformation     | FilterConfigUnicodeTransformation     | Encode the message for conversion   |

Sender：

| title | Configuration class name | Description |
| --------      | -----                | :----  |
| Fs            | SenderConfigFs      | Read a message from a disk file  |
| Ftp           | SenderConfigFtp     | Send messages using the Ftp/Ftps protocol   |
| Http          | SenderConfigHttp    | Send a message using the http/https protocol  |
| Ftp           | SenderConfigFtp     | Send messages using the Ftp/Ftps protocol |
| SFtp          | SenderConfigSftp    | Send messages using the SFtp protocol |
| Email         | SenderConfigEmail   | Send a message using Email    |
| Jms           | SenderConfigJms     | Send messages using the Jms protocol |
| Kafka         | SenderConfigKafka   | Send messages using the Kafka protocol   |
| IbmMq         | SenderConfigIbmMq   | Send messages using the IbmMq protocol   |
| RabbitMq      | SenderConfigRabbitMq| Send messages using the RabbitMq protocol   |
| RocketMq      | SenderConfigRocketMq| Send messages using the RocketMq protocol   |
| ActiveMq      | SenderConfigActiveMq| Send messages using the ActiveMq protocol   |
| Hdfs          | SenderConfigHdfs    | Send messages using the HDFS protocol  |
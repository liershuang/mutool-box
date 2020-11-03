TransferTool

传输工具目前支持功能如下：

Receiver接收器：

| 标题 | 配置类名 | 说明 |
| ------------- | ------------- | ------------- |
| Fs            | ReceiverConfigFs      | 从磁盘文件中读取消息    |
| Ftp           | ReceiverConfigFtp     | 使用Ftp/Ftps协议中接收消息    |
| Http          | ReceiverConfigHttp    | 使用http/https协议接收消息(支持拉取模式和提供Restfull接口方式) |
| Ftp           | ReceiverConfigFtp     | 使用Ftp/Ftps协议接收消息   |
| SFtp          | ReceiverConfigSftp    | 使用SFtp协议接收消息       |
| Email         | ReceiverConfigEmail   | 使用Email协议接收消息          |
| Jms           | ReceiverConfigJms     | 使用Jms协议接收消息        |
| Kafka         | ReceiverConfigKafka   | 使用Kafka协议接收消息        |
| IbmMq         | ReceiverConfigIbmMq   | 使用IbmMq协议接收消息        |
| RabbitMq      | ReceiverConfigRabbitMq| 使用RabbitMq协议接收消息     |
| RocketMq      | ReceiverConfigRocketMq| 使用RocketMq协议接收消息     |
| ActiveMq      | ReceiverConfigActiveMq| 使用ActiveMq协议接收消息     |
| Hdfs          | ReceiverConfigHdfs    | 使用HDFS协议接收消息         |

Filter处理器：

| 标题          | 配置类名                   |  说明  |
| --------      | -----                    | :----  |
| Backup        | FilterConfigBackup        | 将消息备份到文件系统中   |
| Compress      | FilterConfigCompress      | 将消息进行压缩操作       |
| Decompress    | FilterConfigDecompress    | 将消息进行解压操作       |
| EncryptDecrypt| FilterConfigEncryptDecrypt| 将消息进行加密解密操作   |
| OracleSqlldr  | FilterConfigOracleSqlldr  | 将消息存入Oracle数据库   |
| GroovyScript  | FilterConfigGroovyScript  | 将执行Groovy脚本   |
| PythonScript  | FilterConfigPythonScript  | 将执行Python脚本   |
| JavaScript    | FilterConfigJavaScript    | 将执行JavaScript脚本   |
| LuaScript     | FilterConfigLuaScript     | 将执行Lua脚本   |
| UnicodeTransformation     | FilterConfigUnicodeTransformation     | 将消息编码进行转换   |
| ChangeStr     | FilterConfigChangeStr     | 将消息内容进行替换操作   |

Sender发送器：

| 标题          | 配置类名               |  说明  |
| --------      | -----                | :----  |
| Fs            | SenderConfigFs      | 从磁盘文件中读取消息    |
| Ftp           | SenderConfigFtp     | 使用Ftp/Ftps协议中发送消息    |
| Http          | SenderConfigHttp    | 使用http/https协议发送消息  |
| Ftp           | SenderConfigFtp     | 使用Ftp/Ftps协议发送消息   |
| SFtp          | SenderConfigSftp    | 使用SFtp协议发送消息       |
| Email         | SenderConfigEmail   | 使用Email中发送消息          |
| Jms           | SenderConfigJms     | 使用Jms协议发送消息        |
| Kafka         | SenderConfigKafka   | 使用Kafka协议发送消息        |
| IbmMq         | SenderConfigIbmMq   | 使用IbmMq协议发送消息        |
| RabbitMq      | SenderConfigRabbitMq| 使用RabbitMq协议发送消息     |
| RocketMq      | SenderConfigRocketMq| 使用RocketMq协议发送消息     |
| ActiveMq      | SenderConfigActiveMq| 使用ActiveMq协议发送消息     |
| Hdfs          | SenderConfigHdfs    | 使用HDFS协议发送消息     |

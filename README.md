## 中文简易版使用说明

### 1. 采购清单
+ PMS5003ST传感器

    PM、甲醛、温湿度三合一
+ 七星虫usb转串口，或者其他亦可

    这个型号的优点是有信号电平3.3V和5V可选（最上方三个PIN是电压选择区，5V、VCC、3.3V），并且最重要的是使用3.3V信号时（电压选择区用跳线帽连接VCC和3.3V），刚好会空出一个5V的供电PIN

+ 杜邦线3根，透明胶布、指甲刀等

    用于连接设备、处理线材

### 2. 硬件连接
+ usb转串口

    插到电脑上即可，至少Windows 10可以自动识别，一般为COM3，换位置插可能变为COM4或其他，在设备管理器里可以看到。

+ PMS5003ST

    只需要3根线，5V VCC， GND，还有 3.3V 的信号线。建议以后店家赠送定制版的3针转接线，红色VCC，黑色GND，其他颜色信号线，这样可防止新手弄坏东西。

    接线规则：

    ![image](https://github.com/asdawn/PMS5003S/raw/master/images/conn.png)

    `1. 5V VCC，请连接5V供电针`

    `2. GND，请连接GND`

    `5. 3.3V TXD（信号发送），确保转接器信号电平设置为3.3V，连接RXD针（信号接收）`

+ 排线和杜邦线

    可以剪断连接，也可以大力出奇迹直接从拍线口硬拉出来，效果也可以。硬拉容易被割到手，加工时一定要小心。然后用透明胶布裹好导线，当然有电工胶布更好。

### 3. 简易读取程序

#### 3.1 python版
+ 运行环境

    python，pyseril。如果使用了老版本的python，在Windows下安装可能稍麻烦，可能需要先安装pip和easy_install。具体自己搜吧。
+ 读取程序

    见  https://github.com/HaishengLiang/pms5003ST 。请注意修改USB端口名称。

#### 3.2 Java简易版
+ 运行环境

    JRE,还有RXTX库（http://rxtx.qbang.org/pub/rxtx/rxtx-2.2pre2-bins.zip ，Windows下，找到与JRE对应的dll（32位对32位，64位对64位），粘贴到JRE安装目录的`bin`文件夹下）。

+ 读取程序

    见本项目`src/base/PMS5003ST.java`，依赖于RXTX的jar。也是需要注意修改端口名称。

    64位Windows下打包好的程序见本项目的`bin`，仅考虑了COM3和COM4。

### 4. TODO

+ 将读取结果封装为类
+ 使用建议窗口，可以直接双击jar运行
+ 创建Web服务版本
+ 开始进行错误处理
+ 线程优化

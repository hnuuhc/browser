谷歌浏览器工具
===========

### 说明:

1.一键获取本地浏览器的数据,理论上chromium内核都支持  
2.home()方法,默认win版edge用户路径,其它浏览器添加参数,路径至User Data目录

### 简单示例:

```
// 获取本地浏览器cookie   
Map<String, String> cookies = LocalCookie.home().getForDomain("pixiv.net");
// 获取LoginData(账号和密码)  
Map<String, String> loginDatas = LocalLoginData.home().getForDomain("pixiv.net");
// 获取 Local Storage  
Map<String, String> storages = LocalStorage.home().getForDomain("pixiv.net");

```

## 鸣谢

感谢[**JetBrains**](https://www.jetbrains.com/zh-cn/community/opensource/#support)提供的开源开发许可证，JetBrains 通过为核心项目贡献者免费提供一套一流的开发者工具来支持非商业开源项目。

[<img src="https://www.jetbrains.com/icon.svg" width="200"/>](https://www.jetbrains.com/zh-cn/community/opensource/#support)

mvn install:install-file -Dfile="D:\json-lib-2.4-jdk15.jar" -DgroupId=net.sf.json-lib -DartifactId=json-lib -Dversion=2.4-jdk15 -Dpackaging=jar

<!-- 阿里云 依赖 -->
<!-- 蚂蚁金服 依赖 -->
<json-lib.version>2.4-jdk15</json-lib.version>

<dependency>
    <groupId>net.sf.json-lib</groupId>
    <artifactId>json-lib</artifactId>
    <version>2.4-jdk15</version>
</dependency>
    
出现错误：Could not initialize class net.sf.json.util.JSONUtils
则须有：
<!-- https://mvnrepository.com/artifact/net.sf.ezmorph/ezmorph -->
<dependency>
    <groupId>net.sf.ezmorph</groupId>
    <artifactId>ezmorph</artifactId>
    <version>1.0.6</version>
</dependency>
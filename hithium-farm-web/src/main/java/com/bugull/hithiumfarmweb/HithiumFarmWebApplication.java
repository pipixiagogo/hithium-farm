package com.bugull.hithiumfarmweb;

import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.utils.PropertyUtil;
import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableScheduling
public class HithiumFarmWebApplication {
    private static final Logger log = LoggerFactory.getLogger(HithiumFarmWebApplication.class);
    private static final Map<String, String> ARGS = new HashMap<>();
    public static final String HOST_NAME_SPLIT = ",";
    public static final String HOST_PORT_SPLIT = ":";

    public static void main(String[] args) {
        //init mongodb
        String configFilePath = null;
        //加载配置文件
        loadArgs(args);
        String location = ARGS.get("spring.config.location");
        String name = ARGS.get("spring.config.name");
        if (!StringUtils.isEmpty(location)) {
            if (StringUtils.isEmpty(name)) {
                configFilePath = location + File.separator + PropertyUtil.CONFIG;
            } else {
                configFilePath = location + File.separator + name;
            }
        }
        PropertyUtil.init(configFilePath);
        String mongoHosts = PropertyUtil.getProperty(PropertiesConfig.MONGO_HOST);
        String[] hosts = mongoHosts.split(HOST_NAME_SPLIT);
        List<ServerAddress> addresses = new ArrayList<>();
        for (String host : hosts) {
            String[] hostPort = host.split(HOST_PORT_SPLIT);
            Integer port = Integer.parseInt(hostPort[1]);
            addresses.add(new ServerAddress(hostPort[0], port));
        }
        String mongoDB = PropertyUtil.getProperty(PropertiesConfig.MONGO_DB);
        String mongoUser = PropertyUtil.getProperty(PropertiesConfig.MONGO_USERNAME);
        String mongoPwd = PropertyUtil.getProperty(PropertiesConfig.MONGO_PASSWORD);
        BuguConnection mongoConnection = BuguFramework.getInstance().createConnection();
        mongoConnection.setServerList(addresses)
                .setUsername(mongoUser)
                .setPassword(mongoPwd)
                .setDatabase(mongoDB)
                .connect();
        log.info("------------------连接mongodb完成------------------");
        SpringApplication.run(HithiumFarmWebApplication.class, args);
    }

    private static void loadArgs(String[] args) {
        for (String arg : args) {
            String[] kvs = arg.split("=");
            if (kvs.length == 2) {
                ARGS.put(kvs[0], kvs[1]);
            }
        }
    }


}

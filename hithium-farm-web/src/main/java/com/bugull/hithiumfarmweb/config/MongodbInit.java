package com.bugull.hithiumfarmweb.config;

import com.bugull.hithiumfarmweb.utils.PropertyUtil;
import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MongodbInit implements ApplicationContextInitializer {
    private static final Logger log = LoggerFactory.getLogger( MongodbInit.class );

    private static final String HOST_NAME_SPLIT = ",";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        //init mongodb
        String mongoHosts = PropertyUtil.getProperty(PropertiesConfig.MONGO_HOST);
        String[] hosts = mongoHosts.split(HOST_NAME_SPLIT);
        List<ServerAddress> addresses = new ArrayList<>();
        for (String host : hosts){
            String[] hostPort = host.split(":");
            Integer port = Integer.parseInt(hostPort[1]);
            addresses.add(new ServerAddress(hostPort[0], port));
        }
        //int mongoPort = PropertyUtil.getInteger(PropertiesConfig.MONGO_PORT);
        String mongoDB = PropertyUtil.getProperty(PropertiesConfig.MONGO_DB);
        String mongoUser = PropertyUtil.getProperty(PropertiesConfig.MONGO_USERNAME);
        String mongoPwd = PropertyUtil.getProperty(PropertiesConfig.MONGO_PASSWORD);
        BuguConnection mongoConnection = BuguFramework.getInstance().createConnection();
        mongoConnection.setServerList(addresses)
                .setUsername(mongoUser)
                .setPassword(mongoPwd)
                .setDatabase(mongoDB)
                .connect();
        log.info("------------连接mongodb完成------------");
    }
}

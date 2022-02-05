package de.lutz.smartheating;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

	public static Map<String,String> URL_UPONOR_API = new HashMap<String,String>();
	public static String KNX_IP;
	public static int KNX_PORT;
	
	public static Boolean USE_PROXYMAP;
	public static int PROXY_SECONDS;
	
	public static Boolean USE_INFLUXDB;
    public static String INFLUXDB_URL;
    public static String INFLUXDB_USERNAME;
    public static String INFLUXDB_PASSWORD;
    public static String INFLUXDB_DBNAME;
	
	static {
		URL_UPONOR_API.put("OG", "http://192.168.1.128/api");
		URL_UPONOR_API.put("EG", "http://192.168.1.128/api");
		KNX_IP = "192.168.1.165";
		KNX_PORT = 3671;
	}

	@Value("#{${url.uponor.api}}")
	public void setURL_UPONOR_API(Map<String,String> uRL_UPONOR_API) {
		URL_UPONOR_API = new HashMap<String,String>();
		
		URL_UPONOR_API = uRL_UPONOR_API;
	}
	
	@Value("${knx.ip}")
	public void setKNX_IP(String kNX_IP) {
		KNX_IP = kNX_IP;
	}
	
	@Value("${knx.port}")
	public void setKNX_PORT(int kNX_PORT) {
		KNX_PORT = kNX_PORT;
	}
	
    @Value("${influxdb.url}")
    public void setINFLUXDB_URL(String iNFLUXDB_URL) {
        INFLUXDB_URL = iNFLUXDB_URL;
    }

    @Value("${influxdb.username}")
    public void setINFLUXDB_USERNAME(String iNFLUXDB_USERNAME) {
        INFLUXDB_USERNAME = iNFLUXDB_USERNAME;
    }

    @Value("${influxdb.password}")
    public void setINFLUXDB_PASSWORD(String iNFLUXDB_PASSWORD) {
        INFLUXDB_PASSWORD = iNFLUXDB_PASSWORD;
    }

    @Value("${influxdb.dbname}")
    public void setINFLUXDB_DBNAME(String iNFLUXDB_DBNAME) {
        INFLUXDB_DBNAME = iNFLUXDB_DBNAME;
    }
	
    @Value("${influxdb.use}")
    public void setUSE_INFLUXDB(Boolean uSE_INFLUXDB) {
    	USE_INFLUXDB = uSE_INFLUXDB;
    }
    
    @Value("${knx.proxy.use}")
    public void setUSE_PROXYMAP(Boolean uSE_PROXYMAP) {
    	USE_PROXYMAP = uSE_PROXYMAP;
    }
    
	@Value("${knx.proxy.interval.seconds}")
	public void setPROXY_SECONDS(int pROXY_SECONDS) {
		PROXY_SECONDS = pROXY_SECONDS;
	}

}

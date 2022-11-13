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
    public static String INFLUXDB_ORG;
    public static String INFLUXDB_TOKEN;
    public static String INFLUXDB_BUCKET;
	
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

    @Value("${influxdb.org}")
    public void setINFLUXDB_ORG(String iNFLUXDB_ORG) {
        INFLUXDB_ORG = iNFLUXDB_ORG;
    }

    @Value("${influxdb.token}")
    public void setINFLUXDB_TOKEN(String iNFLUXDB_TOKEN) {
        INFLUXDB_TOKEN = iNFLUXDB_TOKEN;
    }

    @Value("${influxdb.bucket}")
    public void setINFLUXDB_BUCKET(String iNFLUXDB_BUCKET) {
        INFLUXDB_BUCKET = iNFLUXDB_BUCKET;
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

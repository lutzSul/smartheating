package de.lutz.smartheating;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component()
public class SmartheatingProperties {

	@Value("#{${url.uponor.api}}")
	private Map<String, String> URL_UPONOR_API;

	@Value("${knx.ip}")
	private String KNX_IP;

	@Value("${knx.port}")
	private int KNX_PORT;

	@Value("${knx.proxy.use}")
	private Boolean USE_PROXYMAP;

	@Value("${knx.proxy.interval.seconds}")
	private int PROXY_SECONDS;

	@Value("${influxdb.use}")
	private Boolean USE_INFLUXDB;

	@Value("${influxdb.url}")
	private String INFLUXDB_URL;

	@Value("${influxdb.org}")
	private String INFLUXDB_ORG;

	@Value("${influxdb.token}")
	private String INFLUXDB_TOKEN;

	@Value("${influxdb.bucket}")
	private String INFLUXDB_BUCKET;

	public Map<String, String> getURL_UPONOR_API() {
		return URL_UPONOR_API;
	}

	public void setURL_UPONOR_API(Map<String, String> uRL_UPONOR_API) {
		URL_UPONOR_API = uRL_UPONOR_API;
	}

	public String getKNX_IP() {
		return KNX_IP;
	}

	public void setKNX_IP(String kNX_IP) {
		KNX_IP = kNX_IP;
	}

	public int getKNX_PORT() {
		return KNX_PORT;
	}

	public void setKNX_PORT(int kNX_PORT) {
		KNX_PORT = kNX_PORT;
	}

	public Boolean getUSE_PROXYMAP() {
		return USE_PROXYMAP;
	}

	public void setUSE_PROXYMAP(Boolean uSE_PROXYMAP) {
		USE_PROXYMAP = uSE_PROXYMAP;
	}

	public int getPROXY_SECONDS() {
		return PROXY_SECONDS;
	}

	public void setPROXY_SECONDS(int pROXY_SECONDS) {
		PROXY_SECONDS = pROXY_SECONDS;
	}

	public Boolean getUSE_INFLUXDB() {
		return USE_INFLUXDB;
	}

	public void setUSE_INFLUXDB(Boolean uSE_INFLUXDB) {
		USE_INFLUXDB = uSE_INFLUXDB;
	}

	public String getINFLUXDB_URL() {
		return INFLUXDB_URL;
	}

	public void setINFLUXDB_URL(String iNFLUXDB_URL) {
		INFLUXDB_URL = iNFLUXDB_URL;
	}

	public String getINFLUXDB_ORG() {
		return INFLUXDB_ORG;
	}

	public void setINFLUXDB_ORG(String iNFLUXDB_ORG) {
		INFLUXDB_ORG = iNFLUXDB_ORG;
	}

	public String getINFLUXDB_TOKEN() {
		return INFLUXDB_TOKEN;
	}

	public void setINFLUXDB_TOKEN(String iNFLUXDB_TOKEN) {
		INFLUXDB_TOKEN = iNFLUXDB_TOKEN;
	}

	public String getINFLUXDB_BUCKET() {
		return INFLUXDB_BUCKET;
	}

	public void setINFLUXDB_BUCKET(String iNFLUXDB_BUCKET) {
		INFLUXDB_BUCKET = iNFLUXDB_BUCKET;
	}

}

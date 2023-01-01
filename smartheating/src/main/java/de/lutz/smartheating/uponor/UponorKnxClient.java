package de.lutz.smartheating.uponor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.lutz.smartheating.SmartheatingProperties;
import de.lutz.smartheating.database.InfluxDBAccess;
import de.lutz.smartheating.model.ActuatorStatus;
import de.lutz.smartheating.model.TempData;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

@Component
public class UponorKnxClient implements InitializingBean {

	public ProcessCommunicator processCommunicator;
	KNXNetworkLink knxLink;
	KnxListener knxListener;

	@Autowired
	private SmartheatingProperties props;

	private Map<String, TempData> proxyTemp = new HashMap<String, TempData>();
	private Map<String, ActuatorStatus> proxyActuator = new HashMap<String, ActuatorStatus>();

	public Map<String, TempData> getProxyData() {
		return this.proxyTemp;
	}

	public void renewConnection() {
		if (this.processCommunicator != null) {
			try {
				this.processCommunicator.close();
				this.processCommunicator = null;
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		if (this.knxLink != null) {
			try {
				this.knxLink.close();
				this.knxLink = null;
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		initProcessCommunicator();

	}

	public InfluxDBAccess getInfluxDBAccess() {
		if (this.knxListener != null) {
			return this.knxListener.getInfluxDBAccess();
		}

		return null;
	}

	public void initProcessCommunicator() {

		String localIP = null;

		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			localIP = socket.getLocalAddress().getHostAddress();
		}

		catch (Exception exIp) {

		}

		try {
			InetSocketAddress local = null;

			if (localIP != null) {
				local = new InetSocketAddress(localIP, 0);
			}

			final InetSocketAddress remote = new InetSocketAddress(props.getKNX_IP(), props.getKNX_PORT());
			this.knxLink = KNXNetworkLinkIP.newTunnelingLink(local, remote, false, new TPSettings());

			this.processCommunicator = new ProcessCommunicatorImpl(knxLink);

			if (props.getUSE_INFLUXDB()) {
				InfluxDBAccess influxDBAccess = new InfluxDBAccess(props.getINFLUXDB_URL(), props.getINFLUXDB_TOKEN(),
						props.getINFLUXDB_ORG(), props.getINFLUXDB_BUCKET());
				this.knxListener = new KnxListener(proxyTemp, proxyActuator, influxDBAccess);
			} else {
				this.knxListener = new KnxListener(proxyTemp, proxyActuator);
			}

			processCommunicator.addProcessListener(this.knxListener);

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public UponorKnxClient() {
	}

	public double readDouble(String groupAddress) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator == null) {
			return 0.0d;
		}
		try {
			return processCommunicator.readFloat(new GroupAddress(groupAddress));
		} catch (Exception e) {
			renewConnection();
			try {
				return processCommunicator.readFloat(new GroupAddress(groupAddress));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return 0.0d;
	}

	public double readDoubleViaProxyMap(String groupAddress) {
		TempData tempData = proxyTemp.get(groupAddress);
		if (tempData != null) {
			LocalDateTime now = LocalDateTime.now();
			Duration duration = Duration.between(now, tempData.getTimestamp());
			long diff = Math.abs(duration.toSeconds());
			if (diff <= props.getPROXY_SECONDS()) {
				return tempData.getTemperature().doubleValue();
			}
		}
		return readDouble(groupAddress);
	}

	public int readInt(String groupAddress) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator == null) {
			return 0;
		}
		try {
			return processCommunicator.readControl(new GroupAddress(groupAddress));
		} catch (Exception e) {
			renewConnection();
			try {
				return processCommunicator.readControl(new GroupAddress(groupAddress));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return 0;
	}

	public String readString(String groupAddress) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator == null) {
			return null;
		}
		try {
			return processCommunicator.readString(new GroupAddress(groupAddress));
		} catch (Exception e) {
			renewConnection();
			try {
				return processCommunicator.readString(new GroupAddress(groupAddress));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	public boolean writeDouble(String groupAddress, double value) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator != null) {

			try {
				processCommunicator.write(new GroupAddress(groupAddress), value, false);
				return true;
			} catch (Exception e) {
				renewConnection();
				try {
					processCommunicator.write(new GroupAddress(groupAddress), value, false);
					return true;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean writeValue(String groupAddress, DPTXlator value) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator != null) {

			try {
				processCommunicator.write(new GroupAddress(groupAddress), value);
				return true;
			} catch (Exception e) {
				renewConnection();
				try {
					processCommunicator.write(new GroupAddress(groupAddress), value);
					return true;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean readBoolean(String groupAddress) {
		if (this.processCommunicator == null) {
			initProcessCommunicator();
		}
		if (this.processCommunicator == null) {
			return false;
		}

		try {
			return processCommunicator.readBool(new GroupAddress(groupAddress));
		} catch (Exception e) {
			renewConnection();
			try {
				return processCommunicator.readBool(new GroupAddress(groupAddress));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
	}

	public boolean readBooleanViaProxyMap(String groupAddress) {
		ActuatorStatus actData = proxyActuator.get(groupAddress);
		if (actData != null) {
			LocalDateTime now = LocalDateTime.now();
			Duration duration = Duration.between(now, actData.getTimestamp());
			long diff = Math.abs(duration.toSeconds());
			if (diff <= props.getPROXY_SECONDS()) {
				return actData.getStatus().booleanValue();
			}
		}
		return readBoolean(groupAddress);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initProcessCommunicator();

	}

}

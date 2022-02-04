package de.lutz.smartheating.uponor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import de.lutz.smartheating.Properties;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class UponorKnxClient {

	public ProcessCommunicator processCommunicator;
	KNXNetworkLink knxLink;

	private Map<String, Double> tempSetpoints = new HashMap<String, Double>();

	private Map<String, Double> tempRemoteSetpoints = new HashMap<String, Double>();

	private Map<String, Double> temperatures = new HashMap<String, Double>();

	public Map<String, Double> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<String, Double> temperatures) {
		this.temperatures = temperatures;
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

	public void initProcessCommunicator() {
		try {
			final InetSocketAddress remote = new InetSocketAddress(Properties.KNX_IP, Properties.KNX_PORT);
			this.knxLink = KNXNetworkLinkIP.newTunnelingLink(null, remote, false, new TPSettings());

			this.processCommunicator = new ProcessCommunicatorImpl(knxLink);

			KnxListener listener = new KnxListener(tempSetpoints, temperatures, tempRemoteSetpoints);
			processCommunicator.addProcessListener(listener);

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public UponorKnxClient() {
		initProcessCommunicator();
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
		Double setpoint = tempSetpoints.get(groupAddress);
		if (setpoint != null) {
			return setpoint.doubleValue();
		}
		Double temperature = temperatures.get(groupAddress);
		if (temperature != null) {
			return temperature.doubleValue();
		}
		Double remoteSetpoint = tempRemoteSetpoints.get(groupAddress);
		if (remoteSetpoint != null) {
			return remoteSetpoint.doubleValue();
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

	public Map<String, Double> getTempSetpoints() {
		return tempSetpoints;
	}

	public void setTempSetpoints(Map<String, Double> tempSetpoints) {
		this.tempSetpoints = tempSetpoints;
	}

	public Map<String, Double> getTempRemoteSetpoints() {
		return tempRemoteSetpoints;
	}

	public void setTempRemoteSetpoints(Map<String, Double> tempRemoteSetpoints) {
		this.tempRemoteSetpoints = tempRemoteSetpoints;
	}

}

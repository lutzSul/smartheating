package de.lutz.smartheating.uponor;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import de.lutz.smartheating.Properties;
import de.lutz.smartheating.model.TempData;
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

	private Map<String, TempData> proxyTemp = new HashMap<String, TempData>();

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

	public void initProcessCommunicator() {
		try {
			final InetSocketAddress remote = new InetSocketAddress(Properties.KNX_IP, Properties.KNX_PORT);
			this.knxLink = KNXNetworkLinkIP.newTunnelingLink(null, remote, false, new TPSettings());

			this.processCommunicator = new ProcessCommunicatorImpl(knxLink);

			KnxListener listener = new KnxListener(proxyTemp);
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
		TempData tempData = proxyTemp.get(groupAddress);
		if (tempData != null) {
			LocalDateTime now = LocalDateTime.now();
			Duration duration = Duration.between(now, tempData.getTimestamp());
			long diff = Math.abs(duration.toSeconds());
			if (diff <= Properties.PROXY_SECONDS) {
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

}

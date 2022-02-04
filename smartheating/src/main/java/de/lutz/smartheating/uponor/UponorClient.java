package de.lutz.smartheating.uponor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.lutz.smartheating.model.Params;
import de.lutz.smartheating.model.Properties;
import de.lutz.smartheating.model.UponorRequest;
import de.lutz.smartheating.model.UponorResult;
import de.lutz.smartheating.model.UponorWriteResult;
import de.lutz.smartheating.model._85;

public class UponorClient {

	public static UponorRequest generateRequest(List<Integer> parameter) {
		UponorRequest uRequest = new UponorRequest();
		uRequest.setId(1);
		uRequest.setJsonrpc("2.0");
		uRequest.setMethod("read");

		Params params = new Params();
		uRequest.setParams(params);
		List<de.lutz.smartheating.model.Object> list = new ArrayList<de.lutz.smartheating.model.Object>();
		ListIterator<Integer> listIterator = parameter.listIterator();
		while (listIterator.hasNext()) {
			Integer param = (listIterator.next());
			de.lutz.smartheating.model.Object object = new de.lutz.smartheating.model.Object();
			object.setId(param.toString());
			Properties props = new Properties();
			_85 wert = new _85();
			props.set85(wert);
			object.setProperties(props);
			list.add(object);
		}

		params.setObjects(list);
		return uRequest;
	}

	public static UponorRequest generateWriteRequest(Map<Integer, Double> parameter) {
		UponorRequest uRequest = new UponorRequest();
		uRequest.setId(1);
		uRequest.setJsonrpc("2.0");
		uRequest.setMethod("write");

		Params params = new Params();
		uRequest.setParams(params);
		List<de.lutz.smartheating.model.Object> list = new ArrayList<de.lutz.smartheating.model.Object>();

		for (Map.Entry<Integer, Double> entry : parameter.entrySet()) {
			Integer param = entry.getKey();
			de.lutz.smartheating.model.Object object = new de.lutz.smartheating.model.Object();
			object.setId(param.toString());
			Properties props = new Properties();
			_85 wert = new _85();
			wert.setValue(entry.getValue());
			props.set85(wert);
			object.setProperties(props);
			list.add(object);
		}

		params.setObjects(list);
		return uRequest;
	}

	public static Map<Integer, Integer> getRoomIds(String installation) {
		Map<Integer, Integer> roomIdsWithServerId = new HashMap<Integer, Integer>();
		List<Integer> targetList = new ArrayList<>(UponorHelper.roomIdsByServerId.keySet());
		Map<Integer, Double> result = readValuesFromApi(installation, targetList);

		for (Map.Entry<Integer, Double> entry : result.entrySet()) {
			if (entry.getValue().doubleValue() != 255d) {
				roomIdsWithServerId.put(entry.getKey(), UponorHelper.roomIdsByServerId.get(entry.getKey()));
			}
		}
		return roomIdsWithServerId;
	}

	public static Map<Integer, Double> readValuesFromApi(String installation, List<Integer> parameter) {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();

		UponorRequest request = generateRequest(parameter);

		URL url;
		try {
			String urlStr = de.lutz.smartheating.Properties.URL_UPONOR_API.get(installation);
			url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = gson.toJson(request).getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}

				UponorResult uponorResult = gson.fromJson(response.toString(), UponorResult.class);

				if (uponorResult != null && uponorResult.getResult() != null
						&& uponorResult.getResult().getObjects() != null) {
					try {
						for (de.lutz.smartheating.model.Object obj : uponorResult.getResult().getObjects()) {
							if (obj.getProperties() != null && obj.getProperties().get85() != null) {
								result.put(Integer.valueOf(obj.getId()), obj.getProperties().get85().getValue());

							}

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static boolean writeValuesToApi(String installation, Map<Integer, Double> parameter) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();

		UponorRequest request = generateWriteRequest(parameter);

		URL url;
		try {
			String urlStr = de.lutz.smartheating.Properties.URL_UPONOR_API.get(installation);
			url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				String requestStr = gson.toJson(request);
				byte[] input = requestStr.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}

				UponorWriteResult uponorResult = gson.fromJson(response.toString(), UponorWriteResult.class);

				if (uponorResult != null && uponorResult.getResult() != null && uponorResult.getResult().equals("ok")) {
					return true;
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}

package org.pipseq.rdf.jena.aggregate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.json.simple.JSONValue;

public class CallRest {

	private String jsonContent(Map<String, Object> map) throws IOException {
		StringWriter sw = new StringWriter();
		JSONValue.writeJSONString(map, sw);
		// println sw.toString();
		return sw.toString();
	}

	public void call(Map<String, Object> map) {
		try {

			URL url = new URL("http://localhost:3001/func");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Accept", "application/json");
//			conn.setRequestProperty("path", "/func");
			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("requestContentType", "application/json");

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			String json = jsonContent(map);
			wr.write(json);
			wr.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output = jsonContent(map);
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}

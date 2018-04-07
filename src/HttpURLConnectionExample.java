import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONObject; 

import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionExample {

	private final String USER_AGENT = "Mozilla/5.0";
	private final String ACCESS_TOKEN = "982650248509247489-LcCQhQMyW9fSwQZSxhYamxGIvUxcFIP";

	public static void main(String[] args) throws Exception {
	    
	    System.out.println(getEncodedKeys());
		HttpURLConnectionExample http = new HttpURLConnectionExample();
	
	    String bearer = http.sendPost();
	    http.sendGet(bearer);
		//System.out.println("Testing 1 - Send Http GET request");
		//http.sendGet();*/
		

	}
	
	// HTTP GET request
		private void sendGet(String token) throws Exception {
		    
			String url = "https://twitter.com/search?q=twitterdev%20new%20premium";
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Authorization", "Bearer "+ token);
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			//System.out.println(response.toString());

		}
		
		private String sendPost() throws Exception {

			String url = "https://api.twitter.com/oauth2/token";
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			final String POST_PARAMS = "grant_type=client_credentials";
			
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String encoding = getEncodedKeys();
			con.setRequestProperty  ("Authorization", "Basic " + encoding);
			
			//String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(POST_PARAMS);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			System.out.println(response.toString());
			JSONObject myResponse = new JSONObject(response.toString());
			String token = myResponse.getString("access_token");
	        //System.out.println("result after Reading JSON Response");
			System.out.println();
	        System.out.println(token);
	        return token;

		}
		
		static String getEncodedKeys() {
		    String consumerKey =  "arKJFzlJIw8yhgcpvsdSyUmIS";
		    String consumerSecretKey = "YFkCOJBF5ADIljXGN1ZlZNpi91i2c2Sv8AacZPYJqQuBbifFjO";
		    String concatenation = consumerKey + ":" +consumerSecretKey;
		    byte [] encodedBytes = Base64.getEncoder().encode(concatenation.getBytes());
		    return new String(encodedBytes);
		}
}
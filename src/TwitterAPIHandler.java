import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class TwitterAPIHandler {

	private static final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
	    
	    System.out.println(getEncodedKeys());
	
	    String token = authenticate();
	    for( String s:searchTwitter("nasa",token) )
	    	System.out.println(s);
	}

	/**
	 * Http GET request to twitter api to search for a query
	 * @param authenticationToken OATH token used to authenticate request
	 * @return
	 * @throws Exception
	 */
		static ArrayList<String> searchTwitter(String query, String authenticationToken) throws Exception {
		    
			String url = "https://api.twitter.com/1.1/search/tweets.json?lang=en&count=100&q="+query;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			con.setRequestProperty("Authorization", "Bearer "+ authenticationToken);
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
			JSONObject myResponse = new JSONObject(response.toString());
			//String tweets = myResponse.getString("statuses");
			//JsonWriter write = new JsonWriter();
			//String niceFormattedJson = JSONWriter.formatJson(tweets);
			/**
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(uglyJSONString);
			String prettyJsonString = gson.toJson(je);
			*/
			ArrayList<String> tweetTexts = new ArrayList<>();
			//System.out.println(myResponse.toString(4));
			JSONArray tweets = myResponse.getJSONArray("statuses");
			for(int i = 0; i < tweets.length(); i++) {
				JSONObject tweet = tweets.getJSONObject(i);
				tweetTexts.add(tweet.getString("text"));
			}
			return tweetTexts;
		}

	/**
	 * Sends a POST request to the url with the access keys
	 * @return The OATH token returned
	 * @throws Exception
	 */
	private static String authenticate() throws Exception {

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
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + urlParameters);
			//System.out.println("Response Code : " + responseCode);

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
			JSONObject myResponse = new JSONObject(response.toString());
			String token = myResponse.getString("access_token");
	        //System.out.println("result after Reading JSON Response");
			//System.out.println();
	        //System.out.println(token);
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

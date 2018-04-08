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

public class TwitterAPIHandler {

	private static final String USER_AGENT = "Mozilla/5.0";

	public static String getWordCloud(String searchQuery){	  
		String input = "";
		try {
			
		    System.out.println(getEncodedKeys());
		    String token = authenticate();
		    ArrayList<String> tweets = searchTwitter(searchQuery.toLowerCase(),token);
		    ArrayList<String> wordList = new ArrayList<>();
		    for(String s:tweets) {
		        wordList.addAll(Arrays.asList(s.split(" ")));
		    }
		    input = filterWords(wordList, searchQuery);
		    System.out.println(input);
		    return(wordCloud(input));
		} catch(Exception e) {
			e.printStackTrace();
		}
	   return "";
	}
	
	static String filterWords(ArrayList<String> words, String searchWord) {
	    for(int i = words.size() - 1; i>= 0; i--) {	        
	        words.set(i, words.get(i).trim().replaceAll("[.(),':!-;&?\\\"]", "").toLowerCase());
	        String word = words.get(i);
	        if(word.length() < 4) {
	            words.remove(i);
	        } else if(word.contains("\n") || word.contains("@") || word.contains("#") || word.contains("http") ) {
	            words.remove(i);
	        } /*else if(word.contains(searchWord.toLowerCase())) {
	            words.remove(i);
	        }*/
	    }
	    String result = "";
	    for(int i = 0; i < Math.min(35, words.size()); i++){
	        result += words.get(i) + " ";
	    }
	    return result.trim();
	}

	/**
	 * Http GET request to twitter api to search for a query
	 * @param authenticationToken OATH token used to authenticate request
	 * @return
	 * @throws Exception
	 */
		static ArrayList<String> searchTwitter(String query, String authenticationToken) throws Exception {
		    
			String url = "https://api.twitter.com/1.1/search/tweets.json?lang=en&count=80&q="+query+"-filter:retweets";

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
			//String result = "";
			// System.out.println(myResponse.toString(4));
			JSONArray tweets = myResponse.getJSONArray("statuses");
			for(int i = 0; i < tweets.length(); i++) {
				JSONObject tweet = tweets.getJSONObject(i);
				tweetTexts.add(tweet.getString("text"));
				//result += tweet.getString("text") + " ";
			}
			return tweetTexts;
		}

	/**
	 * Sends a POST request to the url with the access keys
	 * @return The OATH token returned
	 * @throws Exception
	 */

	private static JSONObject postRequest(String url, String POST_PARAMS, Map<String, String> headers) throws Exception {

			
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			for(String key:headers.keySet()) {
			    con.setRequestProperty(key, headers.get(key));
			}
			
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
			
	        //System.out.println("result after Reading JSON Response");
			//System.out.println();
	        //System.out.println(token);
			JSONObject myResponse = new JSONObject(response.toString());
	        return myResponse;

		}
	
	private static String authenticate() throws Exception {
		TreeMap<String, String> map = new TreeMap<String, String>();
		String url = "https://api.twitter.com/oauth2/token";
		String POST_PARAMS = "grant_type=client_credentials";
		String encoding = "Basic " + getEncodedKeys();
		map.put("Authorization", encoding);
		JSONObject value = postRequest(url, POST_PARAMS, map);
		
		
		String token = value.getString("access_token");
		
		return token;
	}
	
	private static String wordCloud(String words) throws Exception {
	    String url = "https://wordcloudservice.p.mashape.com/generate_wc";
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("Content-Type","application/json");
		map.put("Accept","application/json");
		map.put("X-Mashape-Key", "5xO9bGO6iYmshEeDv8LLjp7dBIPrp1rbH3DjsnY1UlstlFAHRs");
		String POST_PARAMS = "{\"f_type\":\"png\",\"width\":800,\"height\":500,\"s_max\":\"7\",\"s_min\":\"1\",\"f_min\":1,\"r_color\":\"TRUE\",\"r_order\":\"TRUE\",\"s_fit\":\"FALSE\",\"fixed_asp\":\"TRUE\",\"rotate\":\"TRUE\",\"textblock\":\""+words+"\"}";
		JSONObject result = postRequest(url, POST_PARAMS, map);
		return result.getString("url");
	}
		
		static String getEncodedKeys() {
		    String consumerKey =  "arKJFzlJIw8yhgcpvsdSyUmIS";
		    String consumerSecretKey = "YFkCOJBF5ADIljXGN1ZlZNpi91i2c2Sv8AacZPYJqQuBbifFjO";
		    String concatenation = consumerKey + ":" +consumerSecretKey;
		    byte [] encodedBytes = Base64.getEncoder().encode(concatenation.getBytes());
		    return new String(encodedBytes);
		}
}

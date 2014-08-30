package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	
	public static void sendHttpQequest(final String address, final HttpCallbackListener httpCallbackListener){
		
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub				
			HttpURLConnection connection=null;		
				try {
					URL url = new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder(); 
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);						
					}	
					if(httpCallbackListener!=null){
						httpCallbackListener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(httpCallbackListener!=null){
						httpCallbackListener.onError(e);
				}} finally {
					if(connection!=null){
						connection.disconnect();
						}
					}
				}}).start();
	
}
}
	

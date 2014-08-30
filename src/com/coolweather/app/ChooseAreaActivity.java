package com.coolweather.app;
import java.util.ArrayList;
import java.util.List;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import model.City;
import model.County;
import model.Province;
import com.coolweather.app.R;
import db.CoolWeatherDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_COUNTY=2;
	public static final int LEVEL_CITY=1;
	private ListView listView;
	private TextView titleText;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	private List<String> datalist=new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private List<Province>provinceList;
	private List<City>cityList;
	private List<County>countyList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
    private String tag="ChooseAreaActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area); 
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);                             
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub				
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(position);
					queryCitys();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}					
			}  	
        	
		});

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        queryProvinces();      
        listView.setAdapter(adapter);
    }

 
  
    private void queryProvinces(){
    	
    	currentLevel=LEVEL_PROVINCE; 	
    	provinceList=coolWeatherDB.loadProvinces();
    //	Log.i(tag, "sadfa3");
    	if(provinceList.size()>0){  
    		datalist.clear();
    		for(Province province:provinceList){    			
    			datalist.add(province.getProvinceName());
    			 Log.i(tag, province.getProvinceName());
    		}
    		 Log.i(tag, datalist.get(0));
    		 adapter.notifyDataSetChanged();
    		// Log.i(tag, "sadfa3");
    		 listView.setSelection(0);    		
    		 titleText.setText("中国");   		
    	}else{ 
    		
    		queryFromServer(currentLevel,null,null);
    		
    	}   	
    }
  
    private void queryCitys() {
 		// TODO Auto-generated method stub
    	cityList=coolWeatherDB.loadCitys(selectedProvince.getProvinceCode());
    	currentLevel=LEVEL_CITY;
    	if(cityList.size()>0){
    		datalist.clear();
    		for(City city:cityList){
    		datalist.add(city.getCityName());
    		}
    	 adapter.notifyDataSetChanged();
    	 listView.setSelection(0); 
   		 titleText.setText(selectedProvince.getProvinceName());
    	}else{
    		queryFromServer(currentLevel,selectedProvince.getProvinceCode(),null);
    	} 	
 	}
 	
 	private void queryCounties() {
 		// TODO Auto-generated method stub
 		countyList=coolWeatherDB.loadCountys(selectedCity.getCityCode());
    	currentLevel=LEVEL_COUNTY;
    	if(countyList.size()>0){
    		datalist.clear();
    		for(County county:countyList){
    		datalist.add(county.getCountyName());
    		}
    	 adapter.notifyDataSetChanged();
   		 titleText.setText(selectedCity.getCityName());
    	}else{
    		queryFromServer(currentLevel,selectedProvince.getProvinceCode(),selectedCity.getCityCode());
    	} 	
 	}



	private void queryFromServer(final int currentlevel,final String provincecode,final String citycode) {
		// TODO Auto-generated method stub
		String address=null;
		if(currentlevel==LEVEL_PROVINCE){
			
			address="http://www.weather.com.cn/data/city3jdata/china.html";
		
						
		}else if (currentlevel==LEVEL_CITY){
			
			address="http://www.weather.com.cn/data/city3jdata/provshi/"+provincecode+".html";	
			
		}else{
			address="http://www.weather.com.cn/data/city3jdata/station/"+provincecode+citycode+".html";	
		}		
		showProgressDialog();
		HttpUtil.sendHttpQequest(address, new HttpCallbackListener(){
		
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub				
				boolean result=false;
				if(currentlevel==LEVEL_PROVINCE){
					result=Utility.handleProvincesResponse(coolWeatherDB, response);
					//Log.i(tag, response);
				}else if (currentlevel==LEVEL_CITY){
					result=Utility.handleCitysResponse(coolWeatherDB, response,Integer.parseInt(provincecode));
				}else{
					result=Utility.handleCountysResponse(coolWeatherDB, response,Integer.parseInt(citycode));
				}				        
			if(result){
				
				ChooseAreaActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub	
						//Log.i(tag, "sadfa1");
						closeProgressDialog();
						//Log.i(tag, "sadfa2");
						if (currentlevel==LEVEL_PROVINCE){						
							queryProvinces();
						}else if(currentlevel==LEVEL_CITY){
							queryCitys();
						}else{
							queryCounties();
						}						
					}
					
				});
	//			Log.i(tag, "sadfa3");
				}
			} 
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
			
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		});
	}



	private void showProgressDialog() {
		// TODO Auto-generated method stub
		
		if (progressDialog==null){			
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		
		progressDialog.show();
		
	}
	private void closeProgressDialog(){
		
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}

   public void onBackPressed(){
	
	   if(currentLevel==LEVEL_COUNTY){
		   queryCitys();
	   }else if(currentLevel==LEVEL_CITY){
		   queryProvinces();
	   }else{
		   finish();
	   }   
    }	
}




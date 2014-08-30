package util;
import model.City;
import model.County;
import model.Province;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {

	/**
	 * deal provinces
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		
		if(!TextUtils.isEmpty(response)){
			
			String []allProvinces=response.split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p:allProvinces){
					String []array=p.split(":");
					Province province =new Province();
					province.setProvinceCode(array[0].replace("\"", "").replace("{", ""));
					province.setProvinceName(array[1].replace("\"", "").replace("}", ""));
					coolWeatherDB.saveProvince(province);
				}
			}
			
			return true;
			
		}
		
		return false;
		
	}
/**
 * deal city	
 * @param coolWeatherDB
 * @param response
 * @return
 */
	
public static boolean handleCitysResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		
		if(!TextUtils.isEmpty(response)){
			
			String []allcitys=response.split(",");
			if(allcitys!=null && allcitys.length>0){
				for(String p:allcitys){
					String []array=p.split(":");
					City city =new City();
					city.setCityName(array[1].replace("\"", "").replace("}", ""));
					city.setCityCode(array[0].replace("\"", "").replace("{", ""));
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
			}
			
			return true;			
		}
		
		return false;		
	}
/**
 * deal county
 * @param coolWeatherDB
 * @param response
 * @param provinceId
 * @return
 */
public static boolean handleCountysResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
	
	if(!TextUtils.isEmpty(response)){
		
		String []allcountys=response.split(",");
		if(allcountys!=null && allcountys.length>0){
			for(String p:allcountys){
				String []array=p.split(":");
				County county =new County();
				county.setCountyName(array[1].replace("\"", "").replace("}", ""));
				county.setCountyCode(array[0].replace("\"", "").replace("{", ""));
				county.setCityId(cityId);
				coolWeatherDB.saveCounty(county);
			}
		}
		
		return true;			
	} 
	
	return false;		
}		
}

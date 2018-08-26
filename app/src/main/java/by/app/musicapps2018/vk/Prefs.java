package by.app.musicapps2018.vk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Prefs {

    Context context;
    private static final String APP_PREFERENCES = "config";
    private static final String APP_PREFERENCES_FIRST = "first_v";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_NAME = "name";
    private static final String APP_PREFERENCES_PHOTO = "photo";
    private static final String APP_PREFERENCES_REVIEW = "review";
    private static final String APP_PREFERENCES_JOIN = "join";
    private static final String APP_PREFERENCES_PATH = "path";
    private SharedPreferences mSettings;

    public Prefs(Context context) {
        this.context = context;
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public int  getFirst(){
        return Integer.parseInt(mSettings.getString(APP_PREFERENCES_FIRST,"0"));
    }

    public void setFirst(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_FIRST, "1");
        editor.apply();
    }

    public int  getID(){
        return Integer.parseInt(mSettings.getString(APP_PREFERENCES_ID,"0"));
    }

    public void setID(String id){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_ID, id);
        editor.apply();
    }

    public String  getPHOTO(){
        return mSettings.getString(APP_PREFERENCES_PHOTO,"0");
    }

    public void setPHOTO(String id){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PHOTO, id);
        editor.apply();
    }

    public String  getNAME(){
        return mSettings.getString(APP_PREFERENCES_NAME,"0");
    }

    public void setNAME(String id){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_NAME, id);
        editor.apply();
    }

    public String  getPATH(){
        return mSettings.getString(APP_PREFERENCES_PATH,"0");
    }

    public void setPATH(String id){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PATH, id);
        editor.apply();
    }

    public void setReview(int pred){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_REVIEW, String.valueOf(pred));
        editor.apply();
    }
    public int getReview(){
        return Integer.parseInt(mSettings.getString(APP_PREFERENCES_REVIEW,"0"));
    }

    public void setJoin(String id){
        SharedPreferences.Editor editor = mSettings.edit();
        Set<String> set = new HashSet<>();
        set = mSettings.getStringSet(APP_PREFERENCES_JOIN, set);
        set.add(id);
        editor.putStringSet(APP_PREFERENCES_JOIN,set);
        editor.apply();
    }

    public Set<String> getJoin(){
        Set<String> set = new HashSet<>();
        //Log.e("PREFS", "ADS count = "+mSettings.getBoolean(APP_PREFERENCES_ADS_COUNT,true));
        return mSettings.getStringSet(APP_PREFERENCES_JOIN,null);
    }

    public boolean isLogin(){

        if(getID() == 0) return false;
        else return true;

    }

}

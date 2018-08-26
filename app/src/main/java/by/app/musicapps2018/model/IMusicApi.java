package by.app.musicapps2018.model;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface IMusicApi {



    @POST("al_audio.php")
    @FormUrlEncoded
    Observable<ResponseBody> alAudio(@Header("Cookie") String str, @FieldMap Map<String, String> map);

    @POST("al_im.php")
    @FormUrlEncoded
    Observable<ResponseBody> getUser(@Header("Cookie") String str, @FieldMap Map<String, String> map);

}

package pt.iscte.daam.bookcase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

import pt.iscte.daam.bookcase.bo.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private CallbackManager callbackManager;
    private UserProfile userProfile;

    public class DownloadPicture extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            try {
                if(url.length > 1)
                    return null;

                URL pictureURL = new URL(url[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(pictureURL.openConnection().getInputStream());
                userProfile.setPicture(bitmap);

                fillUserInformation();

                return null;

            } catch (Exception e) {
                Log.e("UTILS", "Error getting picture with url: " + url[0] + "\nError:" + e.getMessage());
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_profile);

        this.userProfile = new UserProfile();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken == null) {
            final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("user_friends", "email"));

            this.callbackManager = CallbackManager.Factory.create();

            // Callback registration
            loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getUserInfo(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "Cancel!");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.i(TAG, "error!");
                }
            });
        } else {
            getUserInfo(accessToken);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fillUserInformation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textViewName)).setText("Hi " + userProfile.getName());
                ((TextView) findViewById(R.id.textViewEmail)).setText(userProfile.getEmail());
                ((ImageView) findViewById(R.id.imageProfilePicture)).setImageBitmap(userProfile.getPicture());
            }
        });
    }

    private void getUserInfo(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                try {
                    userProfile.setName(user.optString("name"));
                    userProfile.setEmail(user.optString("email"));
                    userProfile.setFacebookId(user.optString("id"));

                    new DownloadPicture().execute(user.getJSONObject("picture").getJSONObject("data").getString("url"));

                    fillUserInformation();
                } catch (Exception e)
                {
                    return;
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");

        request.setParameters(parameters);

        request.executeAsync();
    }
}

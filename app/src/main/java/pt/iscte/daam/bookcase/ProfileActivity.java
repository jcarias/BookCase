package pt.iscte.daam.bookcase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import pt.iscte.daam.bookcase.bo.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private CallbackManager callbackManager;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_profile);

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "email"));

        this.userProfile = new UserProfile();
        this.callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        userProfile.setName(user.optString("name"));
                        userProfile.setEmail(user.optString("email"));
                        userProfile.setFacebookId(user.optString("id"));
                        userProfile.setPicture(user.optString("picture"));

                        fillUserInformation();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");

                request.setParameters(parameters);

                request.executeAsync();

                Log.i(TAG, "Success!");
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fillUserInformation() {
        ((TextView)findViewById(R.id.textViewName)).setText(this.userProfile.getName());
        ((TextView)findViewById(R.id.textViewEmail)).setText(this.userProfile.getEmail());
    }
}

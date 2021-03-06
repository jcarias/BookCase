package pt.iscte.daam.bookcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.bo.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private CallbackManager callbackManager;

    public class DownloadPicture extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            try {
                if(url.length > 1)
                    return null;

                URL pictureURL = new URL(url[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(pictureURL.openConnection().getInputStream());
                UserProfile.setPicture(bitmap, getApplicationContext());

                fillUserInformation(UserProfile.getProfile(getApplicationContext()));

                return null;

            } catch (Exception e) {
                Log.e("UTILS", "Error getting picture with url: " + url[0] + "\nError:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            BackupServiceTask task = new BackupServiceTask();
            task.execute(getResources().getString(R.string.ftpUrl),
                    getResources().getString(R.string.ftpUser),
                    getResources().getString(R.string.ftpPassword),
                    getApplicationContext().getPackageName(),
                    UserProfile.getProfile(getApplicationContext()).getFacebookId(),
                    "RESTORE");

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_profile);

        ((Button)findViewById(R.id.resetDataButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Reset Data")
                        .setMessage("Are you sure you want to delete all the data stored?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                                bd.deleteAllData();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        UserProfile profile = UserProfile.getProfile(getApplicationContext());

        if(accessToken == null || profile == null) {

            final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("user_friends", "email"));

            this.callbackManager = CallbackManager.Factory.create();

            // Callback registration
            loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    ((TextView) findViewById(R.id.textViewName)).setText("Loading...");
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

            BackupServiceTask task = new BackupServiceTask();
            task.execute(getResources().getString(R.string.ftpUrl),
                         getResources().getString(R.string.ftpUser),
                         getResources().getString(R.string.ftpPassword),
                         getApplicationContext().getPackageName(),
                         profile.getFacebookId(),
                         "BACKUP");

            fillUserInformation(profile);
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    UserProfile.logoutProfile(getApplicationContext());

                    ((TextView) findViewById(R.id.textViewName)).setText("");
                    ((TextView) findViewById(R.id.textViewEmail)).setText("");
                    ((ImageView) findViewById(R.id.imageProfilePicture)).setImageBitmap(null);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fillUserInformation(final UserProfile profile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textViewName)).setText("Hi " + profile.getName());
                ((TextView) findViewById(R.id.textViewEmail)).setText(profile.getEmail());
                ((ImageView) findViewById(R.id.imageProfilePicture)).setImageBitmap(profile.getPicture(getApplicationContext()));
            }
        });
    }

    private void getUserInfo(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                try {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setName(user.optString("name"));
                    userProfile.setEmail(user.optString("email"));
                    userProfile.setFacebookId(user.optString("id"));

                    userProfile.saveProfile(getApplicationContext());

                    new DownloadPicture().execute(user.getJSONObject("picture").getJSONObject("data").getString("url"));

                    fillUserInformation(userProfile);
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

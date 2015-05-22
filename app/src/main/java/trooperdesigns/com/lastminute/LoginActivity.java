package trooperdesigns.com.lastminute;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class LoginActivity extends Activity {

	private TextView message;
	private EditText usernameInput;
	private EditText passwordInput;
	private Button loginButton;
	private Dialog progressDialog;

	private String username;
	private String password;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		message = (TextView) findViewById(R.id.message);
		usernameInput = (EditText) findViewById(R.id.username);
		passwordInput = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login_button);

		usernameInput.setText("justin");
		passwordInput.setText("password");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(false);

		ParseACL.setDefaultACL(defaultACL, true);

		// determine if current user is anonymous
		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			// if user is anonymous, send user to login page
		} else {
			// if user is not anonymous
			// get current user data from parse.com
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				// let logged in users stay
				Intent i = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(i);

				finish();
			} else {

			}

		}



		showHashKey(this);

		// local login click
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Locale l = Locale.getDefault();
				username = usernameInput.getText().toString().toLowerCase(l);
				password = passwordInput.getText().toString().toLowerCase(l);

				// TODO Auto-generated method stub
				ParseUser.logInInBackground(username, password, new LogInCallback() {

					@Override
					public void done(ParseUser user, ParseException e) {
						// TODO Auto-generated method stub
						if (user != null) {
							// if user exists and is authenticated
							// send them to main menu gui
							Intent intent = new Intent(LoginActivity.this,
									MainActivity.class);
							startActivity(intent);
							// show login successful toast
							Toast.makeText(getApplicationContext(), "Login successful!",
									Toast.LENGTH_SHORT).show();
							finish();
						} else {
							message.setText("Please try again");
							Toast.makeText(getApplicationContext(),
									"Invalid login credentials", Toast.LENGTH_SHORT).show();
							Log.d("login", e.getLocalizedMessage());
						}
					}

				});
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		//AppEventsLogger.activateApp(this);
	}

	public static void showHashKey(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"com.trooperdesigns.lastminute", PackageManager.GET_SIGNATURES); // Your
																						// package
																						// name
																						// here
			for (android.content.pm.Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		//AppEventsLogger.deactivateApp(this);
	}


}

package com.globalm.platform.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.SignInResponse;
import com.globalm.platform.models.SignUpResponse;
import com.globalm.platform.models.SocialSignInResponse;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.SharedPreferencesUtil;
import com.globalm.platform.utils.SocialUtil;

import net.igenius.customcheckbox.CustomCheckBox;

import static com.globalm.platform.utils.Settings.saveString;
import static com.globalm.platform.utils.Utils.isValidEmail;

public class LoginActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String USER_AGENT_FAKE = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    private int mNetworkCode;
    private boolean mIsLoading;
    private EditText mFieldEmailSignIn;
    private EditText mFieldPasswordSignIn;
    private EditText mFieldNameSurnameSignUp;
    private EditText mFieldEmailSignUp;
    private EditText mFieldPasswordSignUp;
    private EditText mFieldPasswordConfirmationSignUp;
    private ProgressBar mProgressBarSignIn;
    private ProgressBar mProgressBarSignUp;
    private ProgressBar mProgressBarSignInFacebook;
    private ProgressBar mProgressBarSignInLinkedIn;
    private ProgressBar mProgressBarSignInGoogle;
    private ProgressBar mProgressBarSignInTwitter;
    private ProgressBar mProgressBarConnectFacebook;
    private ProgressBar mProgressBarConnectLinkedIn;
    private ProgressBar mProgressBarConnectGoogle;
    private ProgressBar mProgressBarConnectTwitter;
    private TextView mTextSignIn;
    private TextView mTextSignUp;
    private TextView mTextSignInFacebook;
    private TextView mTextSignInLinkedIn;
    private TextView mTextSignInGoogle;
    private TextView mTextSignInTwitter;
    private TextView mTextConnectFacebook;
    private TextView mTextConnectLinkedIn;
    private TextView mTextConnectGoogle;
    private TextView mTextConnectTwitter;
    private ImageView mButtonBack;
    private LinearLayout mLayoutConnectSocialNetworks;
    private LinearLayout mLayoutSignUpFields;
    private LinearLayout mLayoutSignIn;
    private FrameLayout mLayoutSignUp;
    private FrameLayout mButtonSignIn;
    private FrameLayout mButtonSignUpMain;
    private FrameLayout mButtonSignInFacebook;
    private FrameLayout mButtonSignInLinkedIn;
    private FrameLayout mButtonSignInGoogle;
    private FrameLayout mButtonSignInTwitter;
    private FrameLayout mButtonConnectFacebook;
    private FrameLayout mButtonConnectLinkedIn;
    private FrameLayout mButtonConnectGoogle;
    private FrameLayout mButtonConnectTwitter;
    private WebView mWebView;
    private CustomCheckBox mCheckboxAcceptTermsAndConditions;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mButtonSignIn = findViewById(R.id.button_sign_in);
        mButtonSignUpMain = findViewById(R.id.button_sign_up_main);
        mButtonSignInFacebook = findViewById(R.id.button_sign_in_facebook);
        mButtonSignInLinkedIn = findViewById(R.id.button_sign_in_linked_in);
        mButtonSignInGoogle = findViewById(R.id.button_sign_in_google);
        mButtonSignInTwitter = findViewById(R.id.button_sign_in_twitter);
        mButtonConnectFacebook = findViewById(R.id.button_connect_facebook);
        mButtonConnectLinkedIn = findViewById(R.id.button_connect_linked_in);
        mButtonConnectGoogle = findViewById(R.id.button_connect_google);
        mButtonConnectTwitter = findViewById(R.id.button_connect_twitter);
        LinearLayout buttonAcceptTermsAndConditions = findViewById(R.id.button_accept_terms_conditions);
        TextView buttonForgotPassword = findViewById(R.id.button_forgot_password);
        TextView textAcceptTermsAndConditions = findViewById(R.id.text_accept_terms_conditions);
        TextView buttonSignUp = findViewById(R.id.button_sign_up);
        mFieldEmailSignIn = findViewById(R.id.field_email_sign_in);
        mFieldPasswordSignIn = findViewById(R.id.field_password_sign_in);
        mFieldNameSurnameSignUp = findViewById(R.id.field_name_surname_sign_up);
        mFieldEmailSignUp = findViewById(R.id.field_email_sign_up);
        mFieldPasswordSignUp = findViewById(R.id.field_password_sign_up);
        mFieldPasswordConfirmationSignUp = findViewById(R.id.field_password_confirmation_sign_up);
        mButtonBack = findViewById(R.id.button_back);
        mCheckboxAcceptTermsAndConditions = findViewById(R.id.check_box_accept_terms_conditions);
        mLayoutSignIn = findViewById(R.id.layout_sign_in);
        mLayoutSignUp = findViewById(R.id.layout_sign_up);
        mProgressBarSignIn = findViewById(R.id.progress_bar_sign_in);
        mProgressBarSignUp = findViewById(R.id.progress_bar_sign_up);
        mProgressBarSignInFacebook = findViewById(R.id.progress_bar_sign_in_facebook);
        mProgressBarSignInLinkedIn = findViewById(R.id.progress_bar_sign_in_linked_in);
        mProgressBarSignInGoogle = findViewById(R.id.progress_bar_sign_in_google);
        mProgressBarSignInTwitter = findViewById(R.id.progress_bar_sign_in_twitter);
        mProgressBarConnectFacebook = findViewById(R.id.progress_bar_connect_facebook);
        mProgressBarConnectLinkedIn = findViewById(R.id.progress_bar_connect_linked_in);
        mProgressBarConnectGoogle = findViewById(R.id.progress_bar_connect_google);
        mProgressBarConnectTwitter = findViewById(R.id.progress_bar_connect_twitter);
        mTextSignIn = findViewById(R.id.text_sign_in);
        mTextSignUp = findViewById(R.id.text_sign_up);
        mTextSignInFacebook = findViewById(R.id.text_sign_in_facebook);
        mTextSignInLinkedIn = findViewById(R.id.text_sign_in_linked_in);
        mTextSignInGoogle = findViewById(R.id.text_sign_in_google);
        mTextSignInTwitter = findViewById(R.id.text_sign_in_twitter);
        mTextConnectFacebook = findViewById(R.id.text_connect_facebook);
        mTextConnectLinkedIn = findViewById(R.id.text_connect_linked_in);
        mTextConnectGoogle = findViewById(R.id.text_connect_google);
        mTextConnectTwitter = findViewById(R.id.text_connect_twitter);
        mLayoutConnectSocialNetworks = findViewById(R.id.layout_connect_social_networks);
        mLayoutSignUpFields = findViewById(R.id.layout_sign_up_fields);
        mWebView = findViewById(R.id.web_view);
        textAcceptTermsAndConditions.setPaintFlags(textAcceptTermsAndConditions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        buttonSignUp.setPaintFlags(buttonSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mButtonSignIn.setOnClickListener(this);
        mButtonSignInFacebook.setOnClickListener(this);
        mButtonSignInLinkedIn.setOnClickListener(this);
        mButtonSignInGoogle.setOnClickListener(this);
        mButtonSignInTwitter.setOnClickListener(this);
        mButtonConnectFacebook.setOnClickListener(this);
        mButtonConnectLinkedIn.setOnClickListener(this);
        mButtonConnectGoogle.setOnClickListener(this);
        mButtonConnectTwitter.setOnClickListener(this);
        buttonForgotPassword.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        mButtonSignUpMain.setOnClickListener(this);
        buttonAcceptTermsAndConditions.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
        mFieldEmailSignIn.setOnFocusChangeListener(this);
        mFieldPasswordSignIn.setOnFocusChangeListener(this);
        mFieldNameSurnameSignUp.setOnFocusChangeListener(this);
        mFieldEmailSignUp.setOnFocusChangeListener(this);
        mFieldPasswordSignUp.setOnFocusChangeListener(this);
        mFieldPasswordConfirmationSignUp.setOnFocusChangeListener(this);
        mWebView.getSettings().setUserAgentString(USER_AGENT_FAKE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("social")) {
                    setSocialNetworkConnectButtonLoading(mNetworkCode, false);
                    saveString("access_token", "");
                    hideWebView();
                } else if (url.startsWith("https://m.facebook.com/login") ||
                        url.startsWith("https://accounts.google.com/o/oauth2/auth")) {
                    return;
                } else if (url.contains("code=")) {
                    processSocialLoginUrl(url, 5, 4);
                } else if (url.contains("code")) {
                    processSocialLoginUrl(url, 7, 8);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSupportZoom(false);
    }

    @Override
    public void onBackPressed() {
        if (mLayoutSignUp.getVisibility() == View.VISIBLE) {
            mLayoutSignIn.setVisibility(View.VISIBLE);
            mButtonBack.setVisibility(View.GONE);
            mLayoutSignUp.setVisibility(View.GONE);
            mLayoutConnectSocialNetworks.setVisibility(View.GONE);
            mLayoutSignUpFields.setVisibility(View.VISIBLE);
        } else if (mLayoutSignIn.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (mIsLoading) {
            Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.button_sign_in:
                signIn();
                break;

            case R.id.button_back:
                onBackPressed();
                break;

            case R.id.button_sign_in_facebook:
                socialSignIn(SocialUtil.SOCIAL_NETWORK_FACEBOOK);
                break;

            case R.id.button_sign_in_linked_in:
                socialSignIn(SocialUtil.SOCIAL_NETWORK_LINKED_IN);
                break;

            case R.id.button_sign_in_google:
                socialSignIn(SocialUtil.SOCIAL_NETWORK_GOOGLE);
                break;

            case R.id.button_sign_in_twitter:
                socialSignIn(SocialUtil.SOCIAL_NETWORK_TWITTER);
                break;

            case R.id.button_connect_facebook:
                connectNetwork(SocialUtil.SOCIAL_NETWORK_FACEBOOK);
                break;

            case R.id.button_connect_linked_in:
                connectNetwork(SocialUtil.SOCIAL_NETWORK_LINKED_IN);
                break;

            case R.id.button_connect_google:
                connectNetwork(SocialUtil.SOCIAL_NETWORK_GOOGLE);
                break;

            case R.id.button_connect_twitter:
                connectNetwork(SocialUtil.SOCIAL_NETWORK_TWITTER);
                break;

            case R.id.button_forgot_password:
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_accept_terms_conditions:
                Toast.makeText(this, "This will show terms and conditions", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_sign_up:
                mLayoutSignIn.setVisibility(View.GONE);
                mLayoutSignUp.setVisibility(View.VISIBLE);
                mButtonBack.setVisibility(View.VISIBLE);
                break;

            case R.id.button_sign_up_main:
                signUp();
                break;
        }
    }

    private void socialSignIn(int networkCode) {
        mWebView.setVisibility(View.VISIBLE);
        setSocialNetworkSignInButtonLoading(networkCode, true);
        SocialUtil.loginSocial(networkCode, getSocialCallback(networkCode));
    }

    private void processSocialLoginUrl(String url, int startOffset, int endOffset) {
        if (mNetworkCode == SocialUtil.SOCIAL_NETWORK_FACEBOOK) {
            getAccessToken(getCodeFromUrl(url, startOffset, endOffset));
            hideWebView();
        }else {
            getAccessToken(getCodeFromNonFacebookUrl(url));
            hideWebView();
        }
        setSocialNetworkConnectButtonLoading(mNetworkCode, false);
    }

    private String getCodeFromUrl(String url, int startOffset, int endOffset) {
        String a = url.substring(url.indexOf("code") + startOffset);
        return a.substring(0, a.length() - endOffset);
    }

    private String getCodeFromNonFacebookUrl(String url) {
        return url.substring(url.indexOf("code") + 7);
    }

    private void signIn() {
        String mEmailSignIn = mFieldEmailSignIn.getText().toString();
        String mPasswordIn = mFieldPasswordSignIn.getText().toString();
        if (mEmailSignIn.isEmpty()) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(mEmailSignIn)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPasswordIn.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        } else if (mPasswordIn.length() < 8) {
            Toast.makeText(this, "Password must contain at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mIsLoading = true;
        mButtonSignIn.setEnabled(false);
        mTextSignIn.setVisibility(View.GONE);
        mProgressBarSignIn.setVisibility(View.VISIBLE);
        RequestManager.getInstance().signIn(mFieldEmailSignIn.getText().toString(), mFieldPasswordSignIn.getText().toString(), new CallbackListener<SignInResponse>() {
            @Override
            public void onSuccess(SignInResponse signInResponse) {
                if (signInResponse != null) {
                    saveString("access_token", signInResponse.getData().getToken());
                    saveUserId(signInResponse.getData().getItem().getId());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mIsLoading = false;
            }

            @Override
            public void onFailure(Throwable error) {
                mIsLoading = false;
                mButtonSignIn.setEnabled(true);
                mTextSignIn.setVisibility(View.VISIBLE);
                mProgressBarSignIn.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Please enter a valid email address or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserId(int userId) {
        SharedPreferencesUtil.setUserId(userId);
    }

    private void signInForConnection() {
        RequestManager.getInstance().signIn(mFieldEmailSignUp.getText().toString(), mFieldPasswordSignUp.getText().toString(), new CallbackListener<SignInResponse>() {
            @Override
            public void onSuccess(SignInResponse signInResponse) {
                if (signInResponse != null) {
                    mButtonSignUpMain.setEnabled(true);
                    mTextSignUp.setVisibility(View.VISIBLE);
                    mProgressBarSignUp.setVisibility(View.GONE);
                    mLayoutSignUpFields.setVisibility(View.GONE);
                    mLayoutConnectSocialNetworks.setVisibility(View.VISIBLE);
                    resetFields();
                    saveString("access_token", signInResponse.getData().getToken());
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                mIsLoading = false;
            }

            @Override
            public void onFailure(Throwable error) {
                mIsLoading = false;
                mButtonSignUpMain.setEnabled(true);
                mTextSignUp.setVisibility(View.VISIBLE);
                mProgressBarSignUp.setVisibility(View.GONE);
              //  Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUp() {
        int space = mFieldNameSurnameSignUp.getText().toString().indexOf(" ");
        String name;
        String surname = "";
        String emailSignUp = mFieldEmailSignUp.getText().toString();
        String passwordSignUp = mFieldPasswordSignUp.getText().toString();
        String passwordConfirmationSignUp = mFieldPasswordConfirmationSignUp.getText().toString();

        if (space != -1) {
            name = mFieldNameSurnameSignUp.getText().toString().substring(0, space);
            surname = mFieldNameSurnameSignUp.getText().toString().substring(space).trim();
        } else {
            name = mFieldNameSurnameSignUp.getText().toString();
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name (surname is not required)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailSignUp.isEmpty()) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(emailSignUp)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordSignUp.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordConfirmationSignUp.isEmpty()) {
            Toast.makeText(this, "Please enter password confirmation", Toast.LENGTH_SHORT).show();
            return;
        } else if (!passwordSignUp.equals(passwordConfirmationSignUp)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mCheckboxAcceptTermsAndConditions.isChecked()) {
            Toast.makeText(this, "Please read and accept terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        mIsLoading = true;
        mButtonSignUpMain.setEnabled(false);
        mTextSignUp.setVisibility(View.GONE);
        mProgressBarSignUp.setVisibility(View.VISIBLE);
        RequestManager.getInstance().signUp(
                mFieldEmailSignUp.getText().toString(),
                name,
                surname,
                mFieldPasswordSignUp.getText().toString(),
                mFieldPasswordConfirmationSignUp.getText().toString(),
                new CallbackListener<SignUpResponse>() {
            @Override
            public void onSuccess(SignUpResponse signUpResponse) {
                hideLoading();
                if (signUpResponse.isError()) {
                    String message = getString(R.string.an_error_has_occured);
                    SignUpResponse.Validation validation = signUpResponse.getValidation();
                    if (validation.getPassword() != null) {
                        message = validation.getPassword().get(0);
                    } else if (validation.getEmail() != null) {
                        message = validation.getEmail().get(0);
                    } else if (signUpResponse.getMessage() != null
                            && !signUpResponse.getMessage().isEmpty()) {
                        message = signUpResponse.getMessage();
                    }

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    signInForConnection();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                hideLoading();
                showMessage(error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                //  Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideLoading() {
        mIsLoading = false;
        mButtonSignUpMain.setEnabled(true);
        mTextSignUp.setVisibility(View.VISIBLE);
        mProgressBarSignUp.setVisibility(View.GONE);
    }

    private void connectNetwork(int networkCode) {
        mWebView.setVisibility(View.VISIBLE);
        setSocialNetworkConnectButtonLoading(networkCode, true);
        SocialUtil.connectSocialNetwork(networkCode, getConnectNetworkCallback(networkCode));
    }

    private CallbackListener<String> getSocialCallback(int networkCode) {
        return new CallbackListener<String>() {
            @Override
            public void onSuccess(String url) {
                if (url != null) {
                    mWebView.loadUrl(url);
                } else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                //  Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                setSocialNetworkSignInButtonLoading(networkCode, false);
            }
        };
    }

    private CallbackListener<String> getConnectNetworkCallback(int networkCode) {
        return new CallbackListener<String>() {
            @Override
            public void onSuccess(String url) {
                if (url != null) {
                    mWebView.loadUrl(url);
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable error) {
            //    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                setSocialNetworkConnectButtonLoading(networkCode, false);
            }
        };
    }

    private void getAccessToken(String code) {
        RequestManager.getInstance().getAccessToken(code, new CallbackListener<SignInResponse>() {
            @Override
            public void onSuccess(SignInResponse signInResponse) {
                if (signInResponse != null) {
                    saveString("access_token", signInResponse.getData().getToken());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable error) {
              //  Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                setSocialNetworkSignInButtonLoading(mNetworkCode, false);
            }
        });
    }

    private void hideWebView() {
        mWebView.setVisibility(View.GONE);
        mWebView.clearCache(true);
    }

    private void resetFields() {
        mFieldEmailSignIn.setText("");
        mFieldPasswordSignIn.setText("");
        mFieldNameSurnameSignUp.setText("");
        mFieldEmailSignUp.setText("");
        mFieldPasswordSignUp.setText("");
        mFieldPasswordConfirmationSignUp.setText("");
        mCheckboxAcceptTermsAndConditions.setChecked(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.field_email_sign_in:
                mFieldEmailSignIn.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_email_selected : R.drawable.ic_email_unselected, 0, 0, 0);
                break;

            case R.id.field_password_sign_in:
                mFieldPasswordSignIn.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_password_selected : R.drawable.ic_password_unselected, 0, 0, 0);
                break;

            case R.id.field_name_surname_sign_up:
                mFieldNameSurnameSignUp.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_contact_selected : R.drawable.ic_contact_unselected, 0, 0, 0);
                break;

            case R.id.field_email_sign_up:
                mFieldEmailSignUp.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_email_selected : R.drawable.ic_email_unselected, 0, 0, 0);
                break;

            case R.id.field_password_sign_up:
                mFieldPasswordSignUp.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_password_selected : R.drawable.ic_password_unselected, 0, 0, 0);
                break;

            case R.id.field_password_confirmation_sign_up:
                mFieldPasswordConfirmationSignUp.setCompoundDrawablesWithIntrinsicBounds(hasFocus ? R.drawable.ic_password_selected : R.drawable.ic_password_unselected, 0, 0, 0);
                break;
        }
    }

    private void setSocialNetworkConnectButtonLoading(int networkCode, boolean isLoading) {
        mNetworkCode = networkCode;
        mIsLoading = isLoading;
        switch (networkCode) {
            case 0:
                mProgressBarConnectFacebook.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextConnectFacebook.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonConnectFacebook.setEnabled(!isLoading);
                break;

            case 1:
                mProgressBarConnectLinkedIn.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextConnectLinkedIn.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonConnectLinkedIn.setEnabled(!isLoading);
                break;

            case 2:
                mProgressBarConnectGoogle.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextConnectGoogle.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonConnectGoogle.setEnabled(!isLoading);
                break;

            case 3:
                mProgressBarConnectTwitter.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextConnectTwitter.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonConnectTwitter.setEnabled(!isLoading);
                break;
        }
    }

    private void setSocialNetworkSignInButtonLoading(int networkCode, boolean isLoading) {
        mNetworkCode = networkCode;
        mIsLoading = isLoading;
        switch (networkCode) {
            case SocialUtil.SOCIAL_NETWORK_FACEBOOK:
                mProgressBarSignInFacebook.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextSignInFacebook.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonSignInFacebook.setEnabled(!isLoading);
                break;

            case SocialUtil.SOCIAL_NETWORK_GOOGLE:
                mProgressBarSignInLinkedIn.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextSignInLinkedIn.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonSignInLinkedIn.setEnabled(!isLoading);
                break;

            case SocialUtil.SOCIAL_NETWORK_LINKED_IN:
                mProgressBarSignInGoogle.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextSignInGoogle.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonSignInGoogle.setEnabled(!isLoading);
                break;

            case SocialUtil.SOCIAL_NETWORK_TWITTER:
                mProgressBarSignInTwitter.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mTextSignInTwitter.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                mButtonSignInTwitter.setEnabled(!isLoading);
                break;
        }
    }
}

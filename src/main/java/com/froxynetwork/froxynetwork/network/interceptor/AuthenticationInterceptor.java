package com.froxynetwork.froxynetwork.network.interceptor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.OAuth2Dao;
import com.froxynetwork.froxynetwork.network.output.data.OAuth2DataOutput.OAuth2;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MIT License
 *
 * Copyright (c) 2019 FroxyNetwork
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author 0ddlyoko
 */
/**
 * Authentication Interceptor for OAuth2 Token
 */
public class AuthenticationInterceptor implements Interceptor {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	// The OAuth2 Dao
	private OAuth2Dao oauth2Dao;

	// Another retrofit instance for OAuth2
	private Retrofit retrofit;

	// The token
	private String token;
	private Date expirationDate;

	/**
	 * @param clientCredential The clientCredential, provided by
	 *                         {@link Credentials#basic(String, String)}
	 * @param url              The url of the rest server
	 */
	public AuthenticationInterceptor(String clientCredential, String url) {
		retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
				.client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {

					@Override
					public Response intercept(Chain chain) throws IOException {
						return chain.proceed(
								chain.request().newBuilder().header("Authorization", clientCredential).build());
					}
				}).build()).baseUrl(url).build();
		oauth2Dao = retrofit.create(OAuth2Dao.class);
		if (expirationDate == null || expirationDate.before(new Date())) {
			// The authentication Token is invalid.
			askNewToken();
		}
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		if (expirationDate == null || expirationDate.before(new Date())) {
			// The authentication Token is invalid.
			askNewToken();
		}
		Request original = chain.request();

		Request.Builder builder = original.newBuilder().header("Authorization", "Bearer " + token);
		return chain.proceed(builder.build());
	}

	private void askNewToken() {
		for (int i = 0; i <= 10; i++) {
			LOG.info("Asking a new Authentication token, try {}", i);
			try {
				retrofit2.Response<OAuth2> resp = oauth2Dao.askToken("client_credentials").execute();
				OAuth2 body = resp.body();
				if (body == null || body.getAccessToken() == null
						|| "".equalsIgnoreCase(body.getAccessToken().trim())) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Error while getting a new Authentication token, error {}, try {}", resp.code(), i);
						LOG.warn("Got response {}", resp.errorBody().string());
					}
					continue;
				}
				token = body.getAccessToken();
				int time = body.getExpiresIn();
				int marge = (time * 3) / 4;
				GregorianCalendar cal = new GregorianCalendar();
				// 3 / 4 of time
				cal.add(Calendar.SECOND, marge);
				expirationDate = cal.getTime();
				if (LOG.isInfoEnabled()) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					LOG.info("Authentication token done, expire in {} seconds, marge = {} seconds ({})", time, marge,
							format.format(cal.getTime()));
				}
				return;
			} catch (IOException ex) {
				LOG.error("Error while asking new token: ", ex);
			}
		}
		// TODO Find a better Exception
		throw new IllegalStateException("Cannot retrieve a new Authentication Token !");
	}

	/**
	 * @return true if the token is expired
	 */
	public boolean isTokenExpired() {
		return expirationDate == null || expirationDate.before(new Date());
	}
}

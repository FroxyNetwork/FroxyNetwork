package com.froxynetwork.froxynetwork.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.interceptor.AuthenticationInterceptor;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;
import com.google.gson.GsonBuilder;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
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
public final class NetworkManager {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private final OkHttpClient okHttpClient;
	private final Retrofit retrofit;
	private final ServiceManager serviceManager;

	public NetworkManager(String url, String clientId, String clientSecret) {
		okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new AuthenticationInterceptor(Credentials.basic(clientId, clientSecret), url)).build();
		retrofit = new Retrofit.Builder()
				.addConverterFactory(
						GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()))
				.client(okHttpClient).baseUrl(url).build();
		serviceManager = new ServiceManager(retrofit);
	}

	public final ServiceManager getNetworkServiceManager() {
		return serviceManager;
	}

	/**
	 * Shutdown the client
	 */
	public final void shutdown() {
		if (LOG.isDebugEnabled())
			LOG.debug("Shutting down okHttp");
		okHttpClient.dispatcher().executorService().shutdown();
	}
}

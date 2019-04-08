package com.froxynetwork.froxynetwork;

import com.froxynetwork.froxynetwork.network.interceptor.AuthenticationInterceptor;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.Server;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;
import com.google.gson.GsonBuilder;

import lombok.Getter;
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
public class App {
	@Getter
	private static App instance;
	@Getter
	private Retrofit retrofit;
	@Getter
	private ServiceManager serviceManager;

	public App() throws Exception {
		instance = this;
		// TODO URL in config file
		String clientId = "WEBSOCKET_045cfff18fe0ab8393178e7b7826f227";
		String clientSecret = "SECRET_ecfdc21a8d5022e2db64b1315b087aaf";
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new AuthenticationInterceptor(Credentials.basic(clientId, clientSecret))).build();
		retrofit = new Retrofit.Builder()
				.addConverterFactory(
						GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create()))
				.client(okHttpClient).baseUrl("http://localhost/").build();
		serviceManager = new ServiceManager();
		// System.out.println("SENDING ASYNC GET PLAYER");
		// serviceManager.getPlayerService().asyncGetPlayer("0ddlyoko", new
		// Callback<Player>() {
		// @Override
		// public void onResponse(Player response) {
		// System.out.println("ASYNC response: " + response);
		// try {
		// System.out.println(serviceManager.getPlayerService().syncGetPlayer("1ddlyoko"));
		// } catch (RestException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void onFailure(RestException ex) {
		// System.out
		// .println("Error: " + ex.getError().getCode() + ", message: " +
		// ex.getError().getErrorMessage());
		// }
		//
		// @Override
		// public void onFatalFailure(Throwable t) {
		// System.out.println("Error: " + t);
		// }
		// });
		// System.out.println("SENDING SYNC GET PLAYER");
		// // Should generate RestException
		// try {
		// Player response =
		// serviceManager.getPlayerService().syncGetPlayer("2ddlyoko");
		// } catch (RestException ex) {
		// ex.printStackTrace();
		// }
		System.out.println("Async getting server");
		serviceManager.getServerService().asyncGetServer(43, new Callback<Server>() {

			@Override
			public void onResponse(Server response) {
				System.out.println(response);
			};

			@Override
			public void onFailure(RestException ex) {
				ex.printStackTrace();
			}

			@Override
			public void onFatalFailure(Throwable t) {
				t.printStackTrace();
			}
		});
		serviceManager.getServerService().asyncAddServer("koth_1", 20001, new Callback<Server>() {
			@Override
			public void onResponse(Server response) {
				System.out.println(response);
			};

			@Override
			public void onFailure(RestException ex) {
				ex.printStackTrace();
			}

			@Override
			public void onFatalFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}

package com.froxynetwork.froxynetwork;

import java.io.File;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput.Empty;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.config.ServerConfigDataOutput.ServersConfig;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketManager;

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
	private NetworkManager nm;
	private WebSocketManager wsm;

	public App() throws Exception {
		// This is just for TESTING

		// TODO URL in config file
		String url = "https://localhost/";
		String clientId = "WEBSOCKET_5538f57946961ad1c06064b89112d74b";
		String clientSecret = "SECRET_1b49eda57b597a055973dd6f87ac3983";
		wsm = new WebSocketManager("wss://localhost");
		nm = new NetworkManager(url, clientId, clientSecret);
		ServiceManager sm = nm.getNetwork();

		// Retrieve server configuration
		sm.getServerConfigService().asyncGetServerConfig(new Callback<ServerConfigDataOutput.ServersConfig>() {

			@Override
			public void onResponse(ServersConfig response) {
				System.out.println(response);
				try {
					ServersConfig sc = sm.getServerConfigService().syncGetServerConfig("koth");
					System.out.println(sc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					ServersConfig sc = sm.getServerConfigService().syncGetServerConfig("koth_4players");
					System.out.println(sc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// Download file
				File outputDir = new File("G:\\User\\natha\\Desktop\\minecraft\\Servers\\Servers\\Src");
				sm.getServerDownloadService().asyncDownloadServer("koth_4players",
						new File(outputDir, "koth_4players.zip"), new Callback<EmptyDataOutput.Empty>() {

							@Override
							public void onResponse(Empty response) {
								// Not exists
								// Shutdown at the end
								stop();
							}

							@Override
							public void onFailure(RestException ex) {
								// Ok

								try {
									sm.getServerDownloadService().syncDownloadServer("koth",
											new File(outputDir, "koth.zip"));
									System.out.println("DONE");
								} catch (RestException ex2) {
									ex2.printStackTrace();
								} catch (Exception ex2) {
									ex2.printStackTrace();
								}
								// Shutdown at the end
								stop();
							}

							@Override
							public void onFatalFailure(Throwable t) {
								t.printStackTrace();
								// Shutdown at the end
								stop();
							}
						});
			}

			@Override
			public void onFailure(RestException ex) {
				ex.printStackTrace();
				// Shutdown at the end
				stop();
			}

			@Override
			public void onFatalFailure(Throwable t) {
				t.printStackTrace();
				// Shutdown at the end
				stop();
			}
		});
	}

	private void stop() {
		if (nm != null)
			nm.shutdown();
		if (wsm != null)
			wsm.disconnect();
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}

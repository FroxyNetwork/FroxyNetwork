package com.froxynetwork.froxynetwork.network.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.ServerDownloadDao;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput.Empty;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

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
public class ServerDownloadService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private ServerDownloadDao serverDownloadDao;

	public ServerDownloadService(Retrofit retrofit) {
		serverDownloadDao = retrofit.create(ServerDownloadDao.class);
	}

	public void asyncDownloadServer(String id, File output, Callback<Empty> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncDownloadServer: Retrieving server {}", id);
		serverDownloadDao.getServerConfig(id).enqueue(new retrofit2.Callback<ResponseBody>() {

			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (!response.isSuccessful()) {
					// Error
					try {
						// Let's try to get the json error
						String json = response.errorBody().string();
						EmptyDataOutput body;
						try {
							body = new Gson().fromJson(json, EmptyDataOutput.class);
							// Done, call onFailure to handle the error
							if (callback != null)
								callback.onFailure(new RestException(body));
						} catch (Exception ex) {
							if (LOG.isErrorEnabled())
								LOG.error("Error while parsing result from REST server: {}", json);
							// Exception, call Fatal Failure
							onFailure(call, ex);
							return;
						}
					} catch (Exception ex) {
						// Unknown error
						onFailure(call, ex);
						return;
					}
				} else {
					// Ok
					LOG.info("Server contacted, downloading file type {}", id);
					ResponseBody body = response.body();
					InputStream is = null;
					FileOutputStream os = null;
					try {
						try {
							byte[] fileReader = new byte[4096];

							is = body.byteStream();
							os = new FileOutputStream(output);

							while (true) {
								int read = is.read(fileReader);
								if (read == -1)
									break;
								os.write(fileReader, 0, read);
							}
							os.flush();
							// Done
							LOG.info("File {}.zip successfully downloaded !", id);
							callback.onResponse(new EmptyDataOutput().new Empty());
						} catch (IOException ex) {
							onFailure(call, ex);
						} finally {
							if (is != null)
								is.close();
							if (os != null)
								os.close();
						}
					} catch (IOException ex) {
						LOG.error("", ex);
					}
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				if (callback != null)
					callback.onFatalFailure(t);
			}
		});
	}
}

package com.froxynetwork.froxynetwork.network.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.GeneralDataOutput;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;

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
public final class ServiceHelper {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceHelper.class);

	private ServiceHelper() {
	}

	protected static <T extends GeneralDataOutput<U>, U> retrofit2.Callback<T> callback(Callback<U> callback,
			Class<T> clazz) {
		return new retrofit2.Callback<T>() {

			@Override
			public void onResponse(Call<T> call, Response<T> response) {
				T body;
				try {
					body = response(response, clazz);
				} catch (IOException ex) {
					onFailure(call, ex);
					return;
				}
				if (body.isError())
					// (Normally) impossible
					callback.onFailure(new RestException(body));
				else
					callback.onResponse(body.getData());
			}

			@Override
			public void onFailure(Call<T> call, Throwable t) {
				callback.onFatalFailure(t);
			}
		};
	}

	protected static <T extends GeneralDataOutput<U>, U> T response(Response<T> response, Class<T> clazz)
			throws IOException {
		T body = null;
		if (response.isSuccessful()) {
			// OK
			body = response.body();
		} else {
			String json = response.errorBody().string();
			try {
				body = new Gson().fromJson(json, clazz);
			} catch (Exception ex) {
				if (LOG.isErrorEnabled())
					LOG.error("Error while parsing result from REST server: {}", json);
				throw ex;
			}
		}
		if (LOG.isDebugEnabled())
			LOG.debug("Got code {}, body = {}", response.code(), body.toJson());
		return body;
	}
}

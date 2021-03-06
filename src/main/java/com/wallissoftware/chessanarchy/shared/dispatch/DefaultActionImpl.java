/**
 * Copyright 2012 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this rank except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wallissoftware.chessanarchy.shared.dispatch;

import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

/**
 * Base abstract implementation of
 * {@link com.gwtplatform.dispatch.shared.Action}.
 * 
 * @param <R>
 *            The {@link com.gwtplatform.dispatch.shared.Result} type returned.
 */
public abstract class DefaultActionImpl<R extends Result> implements Action<R> {

	@Override
	public boolean equals(final Object obj) {
		return this.getClass().equals(obj.getClass());
	}

	@Override
	public String getServiceName() {
		String className = this.getClass().getName();
		final int namePos = className.lastIndexOf(".") + 1;
		className = com.gwtplatform.dispatch.rpc.shared.ActionImpl.DEFAULT_SERVICE_NAME + className.substring(namePos);

		return "/" + className;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

}
